package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION

class CompiledModuleArtifact internal constructor(
    val moduleClassName: String,
    val dataResourcePath: String?,
    // Shrinker rules a build must apply to keep the module's trap reports whole, or null when it was compiled without
    // trap diagnostics. Without them a shrunk module still reports its traps, but names only the function the host called.
    val shrinkerRules: String? = null,
) {
    companion object {
        const val DATA_RESOURCE_EXTENSION: String = DATA_EXTENSION
    }
}
