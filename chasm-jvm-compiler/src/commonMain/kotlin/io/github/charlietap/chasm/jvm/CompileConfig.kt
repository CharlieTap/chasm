package io.github.charlietap.chasm.jvm

import io.github.charlietap.chasm.config.RuntimeConfig

data class CompileConfig(
    val className: String,
    val target: CompileTarget = CompileTarget.HOTSPOT,
    val runtime: RuntimeConfig = RuntimeConfig(),
)

/**
 * The virtual machine the compiled classes will run on.
 *
 * The target decides two things about the generated code: how it reads and writes the module's linear memory,
 * and how large a generated method may grow before a Wasm function is split across several methods. Each
 * choice suits one VM and works poorly or not at all on the other.
 *
 * A compiled module runs only on the target it was compiled for. `module()` refuses a module compiled for the
 * other target with `ModuleError.Malformed`, so compile once per target when you ship to both.
 */
enum class CompileTarget {

    /**
     * The HotSpot JVM and other desktop or server JVMs.
     *
     * Linear memory is read and written through `ByteBuffer` calls, which these JVMs compile to direct memory
     * access. Methods are split at 8,000 bytes of bytecode, below the size above which HotSpot stops compiling
     * a method.
     *
     * Pick it for servers, desktop applications and anything else that runs on a standard JVM.
     */
    HOTSPOT,

    /**
     * Android's runtime, ART.
     *
     * Linear memory is a `byte[]` read and written through `sun.misc.Unsafe`, which ART compiles to direct
     * memory access. Methods are split at an estimated 5,150 DEX code units, so that the code still fits ART's
     * limits once the class files are converted to DEX.
     *
     * Pick it for code shipped inside an Android app.
     */
    ART,
}
