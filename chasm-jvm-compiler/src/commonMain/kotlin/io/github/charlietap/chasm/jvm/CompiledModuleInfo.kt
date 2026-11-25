package io.github.charlietap.chasm.jvm

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
    val resources: List<String>,
    val shrinkerRules: String,
)

data class Diagnostics(
    val sites: String,
    val retracedLines: String,
)
