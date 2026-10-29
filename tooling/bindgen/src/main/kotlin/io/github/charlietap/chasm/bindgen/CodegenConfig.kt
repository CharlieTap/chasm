package io.github.charlietap.chasm.bindgen

import java.io.Serializable

/** The runtime targeted by generated module bindings. */
enum class CodegenRuntime {
    /**
     * Generates bindings against [io.github.charlietap.chasm.vm.WasmVirtualMachine].
     *
     * This is the default. It supports JVM, Android, Kotlin/Native,
     * Kotlin/JS, and Kotlin/Wasm JS, and allows applications to provide a
     * different virtual-machine implementation. That portability requires the
     * generated bindings to call through the VM abstraction.
     */
    PORTABLE_VM,

    /**
     * Generates bindings directly against Chasm's embedding API.
     *
     * This avoids the portable VM abstraction and accepts Chasm host functions
     * without wrapping them. It supports Chasm's JVM, Android, and
     * Kotlin/Native targets, but does not support Kotlin/JS or Kotlin/Wasm JS.
     */
    CHASM,
}

/** Controls automatic linking of WASI host functions. */
enum class WasiLinking {
    /**
     *
     * Wasi Link is disabled the caller remains responsible for providing every module import.
     */
    DISABLED,

    /**
     * Automatically links supported WASI imports required by the module.
     *
     * Currently, this links required `wasi_snapshot_preview1` imports through
     * the direct Chasm host-function API.
     *
     * This requires [CodegenRuntime.CHASM]. It supports JVM, Android, Linux
     * x64 and ARM64, macOS ARM64, iOS ARM64, and iOS Simulator ARM64. It does
     * not support web, Windows, or Intel Apple targets.
     */
    AUTOMATIC,
}

data class CodegenConfig(
    val generateTypesafeGlobalProperties: Boolean = false,
    val generateTypesafeMemoryProperties: Boolean = false,
    val generateSuspendingFactories: Boolean = false,
    val runtime: CodegenRuntime = CodegenRuntime.PORTABLE_VM,
    val wasi: WasiLinking = WasiLinking.DISABLED,
    val compiledCode: String? = null,
) : Serializable

internal val CodegenConfig.runsCompiledCode: Boolean
    get() = runtime == CodegenRuntime.CHASM && compiledCode != null
