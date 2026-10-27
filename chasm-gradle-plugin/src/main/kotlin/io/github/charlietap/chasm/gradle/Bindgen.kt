package io.github.charlietap.chasm.gradle

import com.squareup.kotlinpoet.FileSpec
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo

internal data class BindingOptions(
    val interfaceName: String,
    val packageName: String,
    val config: CodegenConfig,
    val interfaceVisibility: InterfaceVisibility,
    val implementationVisibility: ImplementationVisibility,
    val factoryVisibility: FactoryVisibility,
    val allocator: ExportedAllocator?,
    val initializers: Set<String>,
    val functions: List<WasmFunction>,
    val ignoredExports: Set<String>,
)

internal interface BindgenLogger {
    fun lifecycle(message: String)

    fun error(message: String)
}

internal interface BindingGenerator {
    fun generate(info: ModuleInfo, options: BindingOptions): List<FileSpec>
}

internal fun BindingGenerator(logger: BindgenLogger): BindingGenerator = KotlinPoetBindingGenerator(logger)

private class KotlinPoetBindingGenerator(
    private val logger: BindgenLogger,
) : BindingGenerator {

    override fun generate(info: ModuleInfo, options: BindingOptions): List<FileSpec> {
        val wasmInterface = WasmInterfaceFactory()(
            interfaceName = options.interfaceName,
            packageName = options.packageName,
            config = options.config,
            info = info,
            allocator = options.allocator,
            initializers = options.initializers,
            wasmFunctions = options.functions,
            ignoredExports = options.ignoredExports,
            logger = logger,
        )
        return WasmInterfaceGenerator()(
            interfaceVisibility = options.interfaceVisibility,
            implementationVisibility = options.implementationVisibility,
            factoryVisibility = options.factoryVisibility,
            wasmInterface = wasmInterface,
            config = options.config,
        )
    }
}
