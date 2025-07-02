package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType

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
    ): List<FunctionParameter> = params.mapIndexed { idx, param ->
        FunctionParameter(
            name = "p$idx",
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
        input.size == 2 && config.transformStrings && input.matchesStringReturnType() -> Scalar.String
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

internal class WasmInterfaceFactory(
    private val formatter: CamelCaseFormatter = CamelCaseFormatter(),
    private val parameterFactory: ParameterFactory = ParameterFactory(),
    private val returnFactory: ReturnFactory = ReturnFactory(),
) {
    operator fun invoke(
        config: CodegenConfig,
        info: ModuleInfo,
        initializers: Set<String>,
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
                        val function = Function(
                            name = formatter(export.name),
                            params = parameterFactory(type.functionType.params.types),
                            returns = returnFactory(config, export.name, type.functionType.results.types, types),
                            implementation = FunctionProxy(export.name),
                        )
                        functions.add(function)
                    } catch (exception: Exception) {
                        logger("Failed to generate function: ${export.name} because: ${exception.message}")
                    }
                }
                is ExternalType.Global -> {
                    try {
                        if (config.generateTypesafeGlobalProperties) {
                            val property = Property(
                                name = formatter(export.name),
                                type = type.globalType.valueType.asType(),
                                const = type.globalType.mutability == Mutability.Const,
                                implementation = GlobalProxy(export.name, type.globalType.valueType.asExecutionValue()),
                            )
                            properties.add(property)
                        }
                    } catch (exception: Exception) {
                        logger("Failed to generate global: ${export.name} because: ${exception.message}")
                    }
                }
                is ExternalType.Memory,
                is ExternalType.Table,
                is ExternalType.Tag,
                -> Unit
            }
        }

        return WasmInterface(
            initializers = initializers,
            functions = functions,
            properties = properties,
            types = types,
        )
    }
}
