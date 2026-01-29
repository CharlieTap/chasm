package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.map
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.File
import java.io.Serializable

interface CodegenWorkParameters : WorkParameters {
    val binaryPath: Property<String>
    val outputDirectoryPath: Property<String>
    val interfaceName: Property<String>
    val packageName: Property<String>
    val interfaceVisibility: Property<String>
    val implementationVisibility: Property<String>
    val configGenerateTypesafeGlobalProperties: Property<Boolean>
    val allocatorAllocationFunction: Property<String>
    val allocatorDeallocationFunction: Property<String>
    val hasAllocator: Property<Boolean>
    val initializers: SetProperty<String>
    val functions: ListProperty<WasmFunctionData>
    val ignoredExports: SetProperty<String>
}

data class WasmFunctionData(
    val name: String,
    val parameters: List<WasmParameterData>,
    val returnType: WasmReturnTypeData,
) : Serializable

data class WasmParameterData(
    val name: String,
    val typeName: String,
    val stringAllocationStrategyFreeAfterCall: Boolean?,
    val stringEncodingStrategy: String?,
) : Serializable

data class WasmReturnTypeData(
    val typeName: String,
    val stringEncodingStrategy: String?,
) : Serializable

abstract class CodegenWorkAction : WorkAction<CodegenWorkParameters> {

    private val logger = Logging.getLogger(CodegenWorkAction::class.java)

    override fun execute() {
        val params = parameters
        val moduleConfig = ModuleConfig(
            decodeNameSection = true,
        )

        val binaryFile = File(params.binaryPath.get())
        val info = module(binaryFile.readBytes(), moduleConfig)
            .map { mod -> moduleInfo(mod) }
            .expect("Failed to find module at path: ${binaryFile.absolutePath}")

        val factory = WasmInterfaceFactory()
        val generator = WasmInterfaceGenerator()
        val pluginLogger = PluginLogger(logger)

        val config = CodegenConfig(
            generateTypesafeGlobalProperties = params.configGenerateTypesafeGlobalProperties.get(),
        )

        val allocator = if (params.hasAllocator.get()) {
            ExportedAllocator(
                params.allocatorAllocationFunction.get(),
                params.allocatorDeallocationFunction.get(),
            )
        } else {
            null
        }

        val functions = params.functions.get().map { it.toDomainFunction() }
        val interfaceVisibility = TypeVisibility.valueOf(params.interfaceVisibility.get())
        val implementationVisibility = TypeVisibility.valueOf(params.implementationVisibility.get())

        val data = factory(
            interfaceName = params.interfaceName.get(),
            packageName = params.packageName.get(),
            config = config,
            info = info,
            allocator = allocator,
            initializers = params.initializers.get(),
            wasmFunctions = functions,
            ignoredExports = params.ignoredExports.get(),
            logger = pluginLogger,
        )

        val specs = generator(interfaceVisibility, implementationVisibility, data)

        val outputDir = File(params.outputDirectoryPath.get())
        specs.forEach { spec ->
            spec.writeTo(outputDir)
        }
    }
}

internal fun WasmFunction.toWorkData(): WasmFunctionData {
    return WasmFunctionData(
        name = name,
        parameters = parameters.map { param ->
            WasmParameterData(
                name = param.name,
                typeName = param.type.toSerializedTypeName(),
                stringAllocationStrategyFreeAfterCall = param.stringAllocationStrategy?.freeAfterCall,
                stringEncodingStrategy = param.stringEncodingStrategy?.name,
            )
        },
        returnType = WasmReturnTypeData(
            typeName = returnType.type.toSerializedTypeName(),
            stringEncodingStrategy = returnType.stringEncodingStrategy?.name,
        ),
    )
}

internal fun WasmFunctionData.toDomainFunction(): WasmFunction {
    return WasmFunction(
        name = name,
        parameters = parameters.map { param ->
            FunctionParameterDefinition(
                name = param.name,
                type = param.typeName.toSerializedType(),
                stringAllocationStrategy = param.stringAllocationStrategyFreeAfterCall?.let { StringAllocationStrategy(it) },
                stringEncodingStrategy = param.stringEncodingStrategy?.let { StringEncodingStrategy.valueOf(it) },
            )
        },
        returnType = ReturnTypeDefinition(
            type = returnType.typeName.toSerializedType(),
            stringEncodingStrategy = returnType.stringEncodingStrategy?.let { StringEncodingStrategy.valueOf(it) },
        ),
    )
}

internal fun Type.toSerializedTypeName(): String {
    return when (this) {
        Scalar.Integer -> "Int"
        Scalar.Long -> "Long"
        Scalar.Float -> "Float"
        Scalar.Double -> "Double"
        Scalar.String -> "String"
        Scalar.Unit -> "Unit"
        is Aggregate -> throw IllegalArgumentException("Aggregate types not supported in WasmFunction definitions")
    }
}

internal fun String.toSerializedType(): Type {
    return when (this) {
        "Int" -> Scalar.Integer
        "Long" -> Scalar.Long
        "Float" -> Scalar.Float
        "Double" -> Scalar.Double
        "String" -> Scalar.String
        "Unit" -> Scalar.Unit
        else -> throw IllegalArgumentException("Unknown type: $this")
    }
}
