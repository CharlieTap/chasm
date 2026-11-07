package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.FileSpec
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo

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
)

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
        val config = options.config
        val tier = config.executionTier
        require(config.wasi != WasiLinking.AUTOMATIC || config.runtime == CodegenRuntime.CHASM) {
            "WasiLinking.AUTOMATIC requires CodegenRuntime.CHASM because the WASI binding uses " +
                "Chasm's direct host-function API."
        }
        require(config.runtime == CodegenRuntime.CHASM || tier is ExecutionTier.Interpreter) {
            "ExecutionTier.Jit and ExecutionTier.Aot require CodegenRuntime.CHASM because they run on " +
                "Chasm's own runtime."
        }
        require(config.runtime == CodegenRuntime.CHASM || !config.usesStoreSettings) {
            "cpu, memory, interrupts, stacktraces and gcStrategy require CodegenRuntime.CHASM because they " +
                "configure Chasm's store."
        }
        require(tier is ExecutionTier.Interpreter || !config.generateSuspendingFactories) {
            "generateSuspendingFactories requires ExecutionTier.Interpreter because the JIT and compiled code " +
                "have no suspending entry points."
        }
        if (tier is ExecutionTier.Aot) {
            require(QUALIFIED_CLASS_NAME.matches(tier.compiledModule)) {
                "compiledModule must name a class as Kotlin source does, such as com.example.aot.WasmModule: " +
                    "\"${tier.compiledModule}\" is not one."
            }
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
        require(tier !is ExecutionTier.Aot || !wasmInterface.linksWasiPreview1) {
            "WasiLinking.AUTOMATIC is not supported for ExecutionTier.Aot yet. Use WasiLinking.DISABLED and " +
                "pass the module's WASI imports."
        }
        val controls = config.storeControls.map { control -> control.name }
        val clashes = wasmInterface.functions.map { function -> function.name }.filter { name -> name in controls }
        require(clashes.isEmpty()) {
            "The module exports ${clashes.distinct().joinToString()}, which the generated store controls also " +
                "name. Leave the export out with ignoredExports."
        }
        return WasmInterfaceGenerator()(
            interfaceVisibility = options.interfaceVisibility,
            implementationVisibility = options.implementationVisibility,
            factoryVisibility = options.factoryVisibility,
            wasmInterface = wasmInterface,
            config = config,
        )
    }
}

private val QUALIFIED_CLASS_NAME = Regex("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*")
