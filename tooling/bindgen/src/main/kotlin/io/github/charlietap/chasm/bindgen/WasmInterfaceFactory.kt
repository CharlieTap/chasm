package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.NameAllocator
import io.github.charlietap.chasm.bindgen.ext.asType
import io.github.charlietap.chasm.bindgen.ext.asValue
import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType

internal class WasmFunctionValidator {
    operator fun invoke(
        function: WasmFunction,
        type: FunctionType,
    ) {

        val expectedParams = function.parameters.flatMap { parameter ->
            convertToNumberTypes(parameter.type, parameter.stringEncodingStrategy)
        }

        if (!matches(expectedParams, type.params.types)) {
            throw IllegalStateException(
                "Function definition defines parameters $expectedParams but actual wasm function expects ${type.params.types}",
            )
        }

        val expectedReturns = function.returnType.let { returnType ->
            convertToNumberTypes(returnType.type, returnType.stringEncodingStrategy)
        }

        if (!matches(expectedReturns, type.results.types)) {
            throw IllegalStateException(
                "Function definition defines return type $expectedReturns but actual wasm function returns ${type.results.types}",
            )
        }

        val nullableGcStringResult = function.returnType.stringEncodingStrategy.storedInGcArray &&
            type.results.types.any { result -> (result as? ValueType.Reference)?.referenceType is ReferenceType.RefNull }

        if (nullableGcStringResult) {
            throw IllegalStateException(
                "Function definition returns a String from a GC array but the wasm function returns a nullable reference; " +
                    "GC array string results must be non-null references",
            )
        }
    }

    private fun matches(
        expected: List<ExpectedValueType>,
        actual: List<ValueType>,
    ): Boolean {
        return expected.size == actual.size && expected.zip(actual).all { (expectedType, actualType) ->
            expectedType.matches(actualType)
        }
    }

    private fun convertToNumberTypes(
        input: Type,
        encodingStrategy: StringEncodingStrategy?,
    ): List<ExpectedValueType> {
        return when (input) {
            is Aggregate -> {
                input.generated.fields.flatMap { field ->
                    convertToNumberTypes(field.type, null)
                }
            }
            Scalar.Integer -> listOf(ExactValueType(ValueType.Number(NumberType.I32)))
            Scalar.Long -> listOf(ExactValueType(ValueType.Number(NumberType.I64)))
            Scalar.Float -> listOf(ExactValueType(ValueType.Number(NumberType.F32)))
            Scalar.Double -> listOf(ExactValueType(ValueType.Number(NumberType.F64)))
            Scalar.String -> {
                when (requireNotNull(encodingStrategy)) {
                    StringEncodingStrategy.POINTER_AND_LENGTH -> listOf(
                        ExactValueType(ValueType.Number(NumberType.I32)),
                        ExactValueType(ValueType.Number(NumberType.I32)),
                    )
                    StringEncodingStrategy.NULL_TERMINATED -> listOf(ExactValueType(ValueType.Number(NumberType.I32)))
                    StringEncodingStrategy.LENGTH_PREFIXED -> listOf(ExactValueType(ValueType.Number(NumberType.I32)))
                    StringEncodingStrategy.PACKED_POINTER_AND_LENGTH -> listOf(ExactValueType(ValueType.Number(NumberType.I64)))
                    StringEncodingStrategy.GC_UTF16_ARRAY -> listOf(GcArrayValueType(PackedType.I16))
                    StringEncodingStrategy.GC_UTF8_ARRAY -> listOf(GcArrayValueType(PackedType.I8))
                }
            }
            Scalar.Unit -> emptyList()
            else -> throw IllegalArgumentException("Unknown type: $input")
        }
    }
}

internal sealed interface ExpectedValueType {
    fun matches(actual: ValueType): Boolean
}

internal data class ExactValueType(
    val type: ValueType,
) : ExpectedValueType {

    override fun matches(actual: ValueType): Boolean {
        return actual == type
    }

    override fun toString(): String {
        return type.toString()
    }
}

internal data class GcArrayValueType(
    val elementType: PackedType,
) : ExpectedValueType {

    override fun matches(actual: ValueType): Boolean {

        val heapType = (actual as? ValueType.Reference)?.referenceType?.heapType
        val arrayType = (heapType as? ConcreteHeapType.Defined)?.definedType?.arrayType()

        return arrayType?.fieldType?.storageType == StorageType.Packed(elementType)
    }

    override fun toString(): String {
        return "(ref (array $elementType))"
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
            val functionsWithStringParams = functions.filter { function ->
                function.params.any { param -> param.stringEncoding is LinearMemoryString }
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
        gcTypes: GcTypeResolver,
    ): List<FunctionParameter> {
        return params.mapIndexed { idx, param ->
            FunctionParameter(
                name = localNames?.getOrNull(idx) ?: "p$idx",
                type = gcTypes.signatureType(param),
            )
        }
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
        gcTypes: GcTypeResolver,
    ): Type {
        return when {
            input.size == 0 -> Scalar.Unit
            input.size == 1 -> gcTypes.signatureType(input.first())
            else -> {
                val type = GeneratedType(
                    name = classNameFormatter(name) + "Result",
                    fields = input.mapIndexed { idx, type ->
                        Field(
                            name = "r$idx",
                            type = gcTypes.signatureType(type),
                        )
                    },
                )
                types.add(type)
                Aggregate(type)
            }
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
        gcTypes: GcTypeResolver,
    ): FunctionReturn {
        return FunctionReturn(
            type = typeFactory(
                name = functionName,
                config = config,
                input = returns,
                types = types,
                gcTypes = gcTypes,
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
        gcTypes: GcTypeResolver,
    ): Function {

        function?.let {
            functionValidator(function, type)
        }

        val params = function?.let { definition ->
            definedParameters(definition, type, gcTypes)
        } ?: parameterFactory(type.params.types, nameData?.localNames, gcTypes)

        val returnType = function?.returnType?.let {
            FunctionReturn(
                type = it.type,
                stringEncodingStrategy = it.stringEncodingStrategy,
            )
        } ?: returnFactory(config, name, type.results.types, types, gcTypes)

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

    private fun definedParameters(
        function: WasmFunction,
        type: FunctionType,
        gcTypes: GcTypeResolver,
    ): List<FunctionParameter> {

        var wasmParameterIndex = 0

        return function.parameters.map { parameter ->

            val strategy = parameter.stringEncodingStrategy
            val stringEncoding = when {
                strategy == null -> null
                strategy.storedInGcArray -> gcArrayString(type.params.types[wasmParameterIndex], gcTypes)
                else -> LinearMemoryString(strategy)
            }

            wasmParameterIndex += if (strategy == StringEncodingStrategy.POINTER_AND_LENGTH) 2 else 1

            FunctionParameter(
                name = parameter.name,
                type = parameter.type,
                stringAllocationStrategy = parameter.stringAllocationStrategy,
                stringEncoding = stringEncoding,
            )
        }
    }

    private fun gcArrayString(
        wasmParameter: ValueType,
        gcTypes: GcTypeResolver,
    ): GcArrayString {

        val referenceType = (wasmParameter as ValueType.Reference).referenceType
        val definedType = requireNotNull(gcTypes.definedType(referenceType))
        val storageType = requireNotNull(definedType.arrayType()).fieldType.storageType as StorageType.Packed

        return GcArrayString(
            typeIndex = gcTypes.typeIndex(definedType),
            elementType = storageType.type,
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
    private val embeddingApiSelector: EmbeddingApiSelector = EmbeddingApiSelector(),
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
        logger: BindgenLogger,
    ): WasmInterface {

        val callConvention = embeddingApiSelector.embeddingApi(config.executionTier).functionCallConvention
        val gcTypes = GcTypeResolver(
            interfaceClassName = ClassName(packageName, interfaceName),
            mapsReferences = callConvention.passesReferences,
        )
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
                        val function = gcTypes.transaction {
                            functionFactory(
                                name = export.name,
                                type = type.functionType,
                                config = config,
                                types = types,
                                function = wasmFunctions.firstOrNull { definition -> definition.name == export.name },
                                nameData = export.nameData as? FunctionNameData,
                                gcTypes = gcTypes,
                            )
                        }
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
            function.params.any { param -> param.stringEncoding is LinearMemoryString } ||
                (function.returns.type == Scalar.String && !function.returns.stringEncodingStrategy.storedInGcArray)
        }
        if (requiresMemory && memoryExports.none { export -> export.name == DEFAULT_MEMORY_EXPORT_NAME }) {
            memoryExports.add(MemoryExport(DEFAULT_MEMORY_EXPORT_NAME, exposed = false))
        }

        val reservedNames = IMPLEMENTATION_PROPERTY_NAMES +
            properties.map { property -> property.name } +
            functions.map(::preparedFunctionPropertyName) +
            callConvention.reservedNames(functions)
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
            linksWasiPreview1 = config.wasi == WasiLinking.AUTOMATIC && info.imports.any { definition ->
                definition.moduleName == WASI_SNAPSHOT_PREVIEW1_MODULE_NAME
            },
            structViews = gcTypes.structViews,
        )
    }

    private companion object {
        private const val WASI_SNAPSHOT_PREVIEW1_MODULE_NAME = "wasi_snapshot_preview1"
    }
}
