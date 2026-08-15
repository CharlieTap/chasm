package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.NameAllocator
import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.gradle.ext.asType
import io.github.charlietap.chasm.gradle.ext.asValue
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType

internal class WasmFunctionValidator {
    operator fun invoke(
        function: WasmFunction,
        type: FunctionType,
    ) {

        val expectedParams = function.parameters.flatMap { parameter ->
            convertToNumberTypes(parameter.type, parameter.stringEncodingStrategy)
        }

        if (expectedParams != type.params.types) {
            throw IllegalStateException(
                "Function definition defines parameters $expectedParams but actual wasm function expects ${type.params.types}",
            )
        }

        val expectedReturns = function.returnType.let { returnType ->
            convertToNumberTypes(returnType.type, returnType.stringEncodingStrategy)
        }

        if (expectedReturns != type.results.types) {
            throw IllegalStateException(
                "Function definition defines return type $expectedReturns but actual wasm function returns ${type.results.types}",
            )
        }
    }

    private fun convertToNumberTypes(
        input: Type,
        encodingStrategy: StringEncodingStrategy?,
    ): List<ValueType> {
        return when (input) {
            is Aggregate -> {
                input.generated.fields.flatMap { field ->
                    convertToNumberTypes(field.type, null)
                }
            }
            Scalar.Integer -> listOf(ValueType.Number(NumberType.I32))
            Scalar.Long -> listOf(ValueType.Number(NumberType.I64))
            Scalar.Float -> listOf(ValueType.Number(NumberType.F32))
            Scalar.Double -> listOf(ValueType.Number(NumberType.F64))
            Scalar.String -> {
                when (requireNotNull(encodingStrategy)) {
                    StringEncodingStrategy.POINTER_AND_LENGTH -> listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))
                    StringEncodingStrategy.NULL_TERMINATED -> listOf(ValueType.Number(NumberType.I32))
                    StringEncodingStrategy.LENGTH_PREFIXED -> listOf(ValueType.Number(NumberType.I32))
                    StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> listOf(ValueType.Number(NumberType.I64))
                }
            }
            Scalar.Unit -> emptyList()
            else -> throw IllegalArgumentException("Unknown type: $input")
        }
    }
}

internal class CamelCaseFormatter {
    operator fun invoke(snakeCase: String): String {
        return snakeCase.removePrefix("_").split('_')
            .mapIndexed { index, word ->
                if (index == 0) word else word.replaceFirstChar { it.uppercaseChar() }
            }.joinToString("")
    }
}

internal class ClassNameFormatter(
    private val camelCaseFormatter: CamelCaseFormatter = CamelCaseFormatter(),
) {
    operator fun invoke(snakeCase: String): String {
        return camelCaseFormatter(snakeCase).let { name ->
            name.replaceFirstChar { it.uppercaseChar() }
        }
    }
}

internal class AllocatorValidator() {
    operator fun invoke(
        allocator: ExportedAllocator?,
        functions: List<Function>,
        info: ModuleInfo,
    ) {
        if (allocator == null) {
            val functionsWithStringParams = functions.filter { fn ->
                fn.params.any { it.type == Scalar.String }
            }
            if (functionsWithStringParams.isNotEmpty()) {
                val names = functionsWithStringParams.joinToString { it.name }
                throw IllegalStateException(
                    "String parameter(s) specified for function(s) $names but no allocator configured. " +
                        "Configure an allocator in the module extension.",
                )
            }
        } else {
            val allocName = allocator.allocationFunction
            val freeName = allocator.deallocationFunction

            val allocExport = info.exports.firstOrNull { it.name == allocName }
                ?: throw IllegalStateException("Allocator function '$allocName' not found in module exports")
            val freeExport = info.exports.firstOrNull { it.name == freeName }
                ?: throw IllegalStateException("Deallocator function '$freeName' not found in module exports")

            val i32 = ValueType.Number(NumberType.I32)

            val allocType = (allocExport.type as? ExternalType.Function)?.functionType
                ?: throw IllegalStateException("Export '$allocName' is not a function")
            val freeType = (freeExport.type as? ExternalType.Function)?.functionType
                ?: throw IllegalStateException("Export '$freeName' is not a function")

            if (allocType.params.types != listOf(i32) || allocType.results.types != listOf(i32)) {
                throw IllegalStateException(
                    "Allocator '$allocName' has wrong signature. Expected (i32) -> i32, " +
                        "found params=${allocType.params.types}, results=${allocType.results.types}",
                )
            }
            if (freeType.params.types != listOf(i32) || freeType.results.types.isNotEmpty()) {
                throw IllegalStateException(
                    "Deallocator '$freeName' has wrong signature. Expected (i32) -> Unit, " +
                        "found params=${freeType.params.types}, results=${freeType.results.types}",
                )
            }
        }
    }
}

internal class ParameterFactory {
    operator fun invoke(
        params: List<ValueType>,
        localNames: List<String>?,
    ): List<FunctionParameter> = params.mapIndexed { idx, param ->
        FunctionParameter(
            name = localNames?.getOrNull(idx) ?: "p$idx",
            type = param.asType(),
        )
    }
}

internal class ReturnTypeFactory(
    private val classNameFormatter: ClassNameFormatter = ClassNameFormatter(),
) {
    operator fun invoke(
        name: String,
        config: CodegenConfig,
        input: List<ValueType>,
        types: MutableList<GeneratedType>,
    ): Type = when {
        input.size == 0 -> Scalar.Unit
        input.size == 1 -> input.first().asType()
        else -> {
            val type = GeneratedType(
                name = classNameFormatter(name) + "Result",
                fields = input.mapIndexed { idx, type ->
                    Field(
                        name = "r$idx",
                        type = type.asType(),
                    )
                },
            )
            types.add(type)
            Aggregate(type)
        }
    }
}

internal class ReturnFactory(
    private val typeFactory: ReturnTypeFactory = ReturnTypeFactory(),
) {
    operator fun invoke(
        config: CodegenConfig,
        functionName: String,
        returns: List<ValueType>,
        types: MutableList<GeneratedType>,
    ): FunctionReturn {
        return FunctionReturn(
            type = typeFactory(
                name = functionName,
                config = config,
                input = returns,
                types = types,
            ),
        )
    }
}

internal class FunctionFactory(
    private val formatter: CamelCaseFormatter = CamelCaseFormatter(),
    private val functionValidator: WasmFunctionValidator = WasmFunctionValidator(),
    private val parameterFactory: ParameterFactory = ParameterFactory(),
    private val returnFactory: ReturnFactory = ReturnFactory(),
) {
    operator fun invoke(
        name: String,
        type: FunctionType,
        config: CodegenConfig,
        types: MutableList<GeneratedType>,
        function: WasmFunction?,
        nameData: FunctionNameData?,
    ): Function {

        function?.let {
            functionValidator(function, type)
        }

        val params = function?.parameters?.map { parameter ->
            FunctionParameter(
                name = parameter.name,
                type = parameter.type,
                stringAllocationStrategy = parameter.stringAllocationStrategy,
                stringEncodingStrategy = parameter.stringEncodingStrategy,
            )
        } ?: parameterFactory(type.params.types, nameData?.localNames)

        val returnType = function?.returnType?.let {
            FunctionReturn(
                type = it.type,
                stringEncodingStrategy = it.stringEncodingStrategy,
            )
        } ?: returnFactory(config, name, type.results.types, types)

        val implementation = function?.returnType?.let {
            FunctionProxy(name)
        } ?: FunctionProxy(name)

        return Function(
            name = formatter(name),
            params = params,
            returns = returnType,
            resultTypes = type.results.types,
            implementation = implementation,
        )
    }
}

internal class GlobalPropertyFactory(
    private val formatter: CamelCaseFormatter = CamelCaseFormatter(),
) {
    operator fun invoke(
        name: String,
        type: GlobalType,
    ): Property {
        return Property(
            name = formatter(name),
            type = type.valueType.asType(),
            const = type.mutability == Mutability.Const,
            implementation = GlobalProxy(name, type.valueType.asValue()),
        )
    }
}

internal data class MemoryExport(
    val name: String,
    val exposed: Boolean,
)

internal class MemoryBindingFactory {
    operator fun invoke(
        exports: List<MemoryExport>,
        reservedNames: Set<String>,
    ): List<MemoryBinding> {
        val names = NameAllocator()
        reservedNames.forEach { name -> names.newName(name, "reserved:$name") }

        val defaultMemory = exports.indexOfFirst { export ->
            export.name == DEFAULT_MEMORY_EXPORT_NAME
        }
        val defaultBackingName = if (defaultMemory >= 0) {
            names.newName(DEFAULT_MEMORY_BACKING_NAME, "backing:$defaultMemory")
        } else {
            null
        }

        val propertyNames = exports.mapIndexed { index, export ->
            if (export.exposed) {
                names.newName(export.name, "property:$index")
            } else {
                export.name
            }
        }

        return exports.mapIndexed { index, export ->
            val backingName = if (index == defaultMemory) {
                requireNotNull(defaultBackingName)
            } else {
                names.newName("_${export.name}", "backing:$index")
            }
            MemoryBinding(
                name = propertyNames[index],
                source = export.name,
                exposed = export.exposed,
                backingName = backingName,
            )
        }
    }
}

private val IMPLEMENTATION_PROPERTY_NAMES = setOf(
    "binary",
    "imports",
    "virtualMachine",
    "moduleFactory",
    "instanceFactory",
    "store",
    "module",
    "allocatedImports",
    "instance",
    "allocator",
)

internal class ExportExcluder() {
    operator fun invoke(
        name: String,
        allocator: ExportedAllocator?,
        ignoredExports: Set<String>,
    ): Boolean {

        val allocatorFunctions = allocator?.let {
            setOf(it.allocationFunction, it.deallocationFunction)
        } ?: emptySet()
        val ignored = ignoredExports + allocatorFunctions

        return ignored.contains(name)
    }
}

internal class WasmInterfaceFactory(
    private val functionFactory: FunctionFactory = FunctionFactory(),
    private val globalPropertyFactory: GlobalPropertyFactory = GlobalPropertyFactory(),
    private val memoryBindingFactory: MemoryBindingFactory = MemoryBindingFactory(),
    private val allocatorValidator: AllocatorValidator = AllocatorValidator(),
    private val exportExcluder: ExportExcluder = ExportExcluder(),
) {
    operator fun invoke(
        interfaceName: String,
        packageName: String,
        config: CodegenConfig,
        info: ModuleInfo,
        allocator: ExportedAllocator?,
        initializers: Set<String>,
        wasmFunctions: List<WasmFunction>,
        ignoredExports: Set<String>,
        logger: Logger,
    ): WasmInterface {

        val functions = mutableListOf<Function>()
        val properties = mutableListOf<Property>()
        val memoryExports = mutableListOf<MemoryExport>()
        val types = mutableListOf<GeneratedType>()

        info.exports.forEach { export ->

            if (exportExcluder(export.name, allocator, ignoredExports)) {
                return@forEach
            }

            when (val type = export.type) {
                is ExternalType.Function -> {
                    try {
                        if (initializers.contains(export.name)) return@forEach
                        val function = functionFactory(
                            name = export.name,
                            type = type.functionType,
                            config = config,
                            types = types,
                            function = wasmFunctions.firstOrNull { it.name == export.name },
                            nameData = export.nameData as? FunctionNameData,
                        )
                        functions.add(function)
                    } catch (exception: Exception) {
                        logger.error("Failed to generate function ${export.name} because ${exception.message}")
                    }
                }
                is ExternalType.Global -> {
                    try {
                        if (config.generateTypesafeGlobalProperties) {
                            val property = globalPropertyFactory(
                                name = export.name,
                                type = type.globalType,
                            )
                            properties.add(property)
                        }
                    } catch (exception: Exception) {
                        logger.error("Failed to generate global ${export.name} because ${exception.message}")
                    }
                }
                is ExternalType.Memory -> {
                    if (config.generateTypesafeMemoryProperties) {
                        memoryExports.add(MemoryExport(export.name, exposed = true))
                    }
                }
                is ExternalType.Table,
                is ExternalType.Tag,
                -> Unit
            }
        }

        val requiresMemory = functions.any { function ->
            function.params.any { it.type == Scalar.String } || function.returns.type == Scalar.String
        }
        if (requiresMemory && memoryExports.none { it.name == DEFAULT_MEMORY_EXPORT_NAME }) {
            memoryExports.add(MemoryExport(DEFAULT_MEMORY_EXPORT_NAME, exposed = false))
        }

        val reservedNames = IMPLEMENTATION_PROPERTY_NAMES +
            properties.map { property -> property.name } +
            functions.map(::preparedFunctionPropertyName)
        val memories = memoryBindingFactory(memoryExports, reservedNames)

        allocatorValidator(allocator, functions, info)

        return WasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            allocator = allocator,
            initializers = initializers,
            functions = functions,
            properties = properties,
            memories = memories,
            types = types,
        )
    }
}
