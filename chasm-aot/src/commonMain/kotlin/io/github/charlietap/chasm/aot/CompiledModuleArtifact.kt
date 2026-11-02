package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.compiler.backend.jvm.DATA_EXTENSION
import java.nio.file.Path

@OptIn(ExperimentalChasmApi::class)
class CompiledModuleArtifact internal constructor(
    val moduleClassName: String,
    val metadata: CompiledModuleMetadata,
    val dataResourcePath: String?,
    val files: List<Path>,
    val shrinkerRules: String,
) {
    companion object {
        const val DATA_RESOURCE_EXTENSION: String = DATA_EXTENSION
    }
}
