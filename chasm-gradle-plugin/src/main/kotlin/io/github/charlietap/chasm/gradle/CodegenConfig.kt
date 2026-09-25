package io.github.charlietap.chasm.gradle

import java.io.Serializable
import kotlin.ExperimentalVersionOverloading
import kotlin.IntroducedAt

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

@OptIn(ExperimentalVersionOverloading::class)
data class CodegenConfig(
    val generateTypesafeGlobalProperties: Boolean = false,
    @IntroducedAt("2.2.0")
    val generateTypesafeMemoryProperties: Boolean = false,
    @IntroducedAt("2.2.0")
    val generateSuspendingFactories: Boolean = false,
    @IntroducedAt("3.0.1")
    val runtime: CodegenRuntime = CodegenRuntime.PORTABLE_VM,
) : Serializable
