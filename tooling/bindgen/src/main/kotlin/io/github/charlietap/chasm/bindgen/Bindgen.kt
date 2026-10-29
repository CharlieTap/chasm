package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.FileSpec
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import java.io.Serializable

data class BindingOptions(
    val interfaceName: String,
    val packageName: String,
    val config: CodegenConfig = CodegenConfig(),
    val interfaceVisibility: InterfaceVisibility = InterfaceVisibility.PUBLIC,
    val implementationVisibility: ImplementationVisibility = ImplementationVisibility.PRIVATE,
    val factoryVisibility: FactoryVisibility = FactoryVisibility.PUBLIC,
    val allocator: ExportedAllocator? = null,
    val initializers: Set<String> = emptySet(),
    val functions: List<WasmFunction> = emptyList(),
    val ignoredExports: Set<String> = emptySet(),
) : Serializable

interface BindgenLogger {
    fun lifecycle(message: String)

    fun error(message: String)

    object None : BindgenLogger {
        override fun lifecycle(message: String) = Unit

        override fun error(message: String) = Unit
    }
}

/** Generates the typesafe Kotlin binding of a Wasm module. */
interface BindingGenerator {

    /** The interface of the module's exports, and the implementation and factory that call them. */
    fun generate(info: ModuleInfo, options: BindingOptions): List<FileSpec>
}

fun BindingGenerator(logger: BindgenLogger = BindgenLogger.None): BindingGenerator = KotlinPoetBindingGenerator(logger)

private class KotlinPoetBindingGenerator(
    private val logger: BindgenLogger,
) : BindingGenerator {

    override fun generate(info: ModuleInfo, options: BindingOptions): List<FileSpec> {
        require(options.config.wasi != WasiLinking.AUTOMATIC || options.config.runtime == CodegenRuntime.CHASM) {
            "WasiLinking.AUTOMATIC requires CodegenRuntime.CHASM because the WASI binding uses " +
                "Chasm's direct host-function API."
        }
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
        require(!options.config.runsCompiledCode || !wasmInterface.linksWasiPreview1) {
            "WasiLinking.AUTOMATIC is not supported for compiled code yet. Use WasiLinking.DISABLED and " +
                "pass the module's WASI imports."
        }
        return WasmInterfaceGenerator()(
            interfaceVisibility = options.interfaceVisibility,
            implementationVisibility = options.implementationVisibility,
            factoryVisibility = options.factoryVisibility,
            wasmInterface = wasmInterface,
            config = options.config,
        )
    }
}
