package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.shapes.FunctionNameData
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.gradle.ext.asExecutionValue
import io.github.charlietap.chasm.gradle.ext.asType
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

        val params = function?.parameters?.map { (name, type) ->
            FunctionParameter(
                name = name,
                type = type,
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
            implementation = GlobalProxy(name, type.valueType.asExecutionValue()),
        )
    }
}

internal class WasmInterfaceFactory(
    private val functionFactory: FunctionFactory = FunctionFactory(),
    private val globalPropertyFactory: GlobalPropertyFactory = GlobalPropertyFactory(),
) {
    operator fun invoke(
        interfaceName: String,
        packageName: String,
        config: CodegenConfig,
        info: ModuleInfo,
        initializers: Set<String>,
        wasmFunctions: List<WasmFunction>,
        logger: (String) -> Unit,
    ): WasmInterface {

        val functions = mutableListOf<Function>()
        val properties = mutableListOf<Property>()
        val types = mutableListOf<GeneratedType>()

        info.exports.forEach { export ->
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
                        logger("Failed to generate function ${export.name} because ${exception.message}")
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
                        logger("Failed to generate global ${export.name} because ${exception.message}")
                    }
                }
                is ExternalType.Memory,
                is ExternalType.Table,
                is ExternalType.Tag,
                -> Unit
            }
        }

        return WasmInterface(
            interfaceName = interfaceName,
            packageName = packageName,
            initializers = initializers,
            functions = functions,
            properties = properties,
            types = types,
        )
    }
}
