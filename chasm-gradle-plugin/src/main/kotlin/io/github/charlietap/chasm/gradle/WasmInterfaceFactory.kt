package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType
import org.jetbrains.kotlin.gradle.utils.property

internal class CamelCaseFormatter {
    operator fun invoke(snakeCase: String): String {
        return snakeCase.split('_')
            .mapIndexed { index, word ->
                if (index == 0) word else word.replaceFirstChar { it.uppercaseChar() }
            }
            .joinToString("")
    }
}

internal class ParameterFactory
{
    operator fun invoke(
         params: List<ValueType>,
    ): List<FunctionParameter> = params.mapIndexed { idx, param ->
        FunctionParameter(
            name = "p$idx",
            type = param,
        )
    }
}

internal class ReturnFactory
{
    operator fun invoke(
        config: CodegenConfig,
        functionName: String,
        returns: List<ValueType>,
    ): FunctionReturn {
        return when {
            returns.size == 0 -> FunctionReturn.Unit
            returns.size == 1 -> FunctionReturn.Primitive(returns.first())
            returns.size == 2 && config.transformStrings && returns.matchesStringReturnType() -> FunctionReturn.String
            else -> FunctionReturn.Type(
                GeneratedType(
                    name = "${functionName}Result",
                    fields = returns.mapIndexed { idx, type ->
                        Field(
                            name = "r$idx",
                            type = type,
                        )
                    }
                )
            )
        }
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
    ): WasmInterface {

        val functions = mutableListOf<Function>()
        val properties = mutableListOf<Property>()
        val types = mutableListOf<GeneratedType>()

        info.exports.forEach { export ->
            when(val type = export.type) {
                is ExternalType.Function -> {
                    val function = Function(
                        name = formatter(export.name),
                        params = parameterFactory(type.functionType.params.types),
                        returns = returnFactory(config, export.name, type.functionType.results.types),
                    )
                    functions.add(function)
                }
                is ExternalType.Global -> {
                    val name = formatter(export.name)
                    val property = Property.GlobalProperty(name)
                    properties.add(property)

                    if(config.generateTypesafeGlobalFunctions) {
                        val globalType = type.globalType

                        val readFunction = Function(
                            name = formatter("read_" + export.name),
                            params = emptyList(),
                            returns = FunctionReturn.Primitive(globalType.valueType)
                        )
                        functions.add(readFunction)

                        if(globalType.mutability == Mutability.Var) {
                            val writeFunction = Function(
                                name = formatter("write_" + export.name),
                                params = listOf(FunctionParameter("global", globalType.valueType)),
                                returns = FunctionReturn.Unit,
                            )
                            functions.add(writeFunction)
                        }
                    }
                }
                is ExternalType.Memory -> {
                    val name = formatter(export.name)
                    val property = Property.MemoryProperty(name)
                    properties.add(property)
                }
                is ExternalType.Table -> {
                    val name = formatter(export.name)
                    val property = Property.TableProperty(name)
                    properties.add(property)
                }
                is ExternalType.Tag -> {
                    val name = formatter(export.name)
                    val property = Property.TagProperty(name)
                    properties.add(property)
                }
            }
        }

        return WasmInterface(
            functions = functions,
            properties = properties,
            types = types,
        )
    }
}
