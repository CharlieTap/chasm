package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION

class CompiledModuleArtifact internal constructor(
    val moduleClassName: String,
    val dataResourcePath: String?,
) {
    companion object {
        const val DATA_RESOURCE_EXTENSION: String = DATA_EXTENSION
    }
}
