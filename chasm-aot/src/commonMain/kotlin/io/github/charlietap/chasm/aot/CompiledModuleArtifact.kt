package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION
import java.nio.file.Path

class CompiledModuleArtifact internal constructor(
    val moduleClassName: String,
    val dataResourcePath: String?,
    // The files a CompileOutput.Directory or CompileOutput.Jar wrote; empty for CompileOutput.Memory.
    val files: List<Path>,
    // Shrinker rules a build must apply to keep the module's trap reports whole, or null when it was compiled without
    // trap diagnostics. Without them a shrunk module still reports its traps, but names only the function the host called.
    val shrinkerRules: String? = null,
) {
    companion object {
        const val DATA_RESOURCE_EXTENSION: String = DATA_EXTENSION
    }
}
