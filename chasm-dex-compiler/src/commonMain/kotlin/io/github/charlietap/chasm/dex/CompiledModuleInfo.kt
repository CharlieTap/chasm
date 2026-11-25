package io.github.charlietap.chasm.dex

import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition

data class CompiledModuleInfo(
    val config: CompileConfig,
    val imports: List<ImportDefinition>,
    val exports: List<ExportDefinition>,
    val runtimeAbi: Int,
    val compilerVersion: String,
    val sourceHash: String,
    val diagnostics: Diagnostics?,
    val classes: List<String>,
    val dexFiles: List<String>,
    val resources: List<String>,
)

data class Diagnostics(
    val sites: String,
)
