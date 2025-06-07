package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.runtime.type.ExternalType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ValueType
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeFirstWord

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
        return when (returns.size) {
            0 -> FunctionReturn.Unit
            1 -> FunctionReturn.Primitive(returns.first())
            2 if config.transformStrings && returns.matchesStringReturnType() -> FunctionReturn.String
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
                        name = export.name,
                        params = parameterFactory(type.functionType.params.types),
                        returns = returnFactory(config, export.name, type.functionType.results.types),
                    )
                    functions.add(function)
                }
                is ExternalType.Global -> {
                    val property = Property.GlobalProperty(export.name)
                    properties.add(property)

                    if(config.generateTypesafeGlobalFunctions) {
                        val globalType = type.globalType

                        val readFunction = Function(
                            name = "read" + export.name.capitalizeFirstWord(),
                            params = emptyList(),
                            returns = FunctionReturn.Primitive(globalType.valueType)
                        )
                        functions.add(readFunction)

                        if(globalType.mutability == Mutability.Var) {
                            val writeFunction = Function(
                                name = "write" + export.name.capitalizeFirstWord(),
                                params = listOf(FunctionParameter("value", globalType.valueType)),
                                returns = FunctionReturn.Unit,
                            )
                            functions.add(writeFunction)
                        }
                    }
                }
                is ExternalType.Memory -> {
                    val property = Property.MemoryProperty(export.name)
                    properties.add(property)
                }
                is ExternalType.Table -> {
                    val property = Property.TableProperty(export.name)
                    properties.add(property)
                }
                is ExternalType.Tag -> {
                    val property = Property.TagProperty(export.name)
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
