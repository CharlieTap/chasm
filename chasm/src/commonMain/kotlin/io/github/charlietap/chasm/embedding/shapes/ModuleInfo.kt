package io.github.charlietap.chasm.embedding.shapes

data class ModuleInfo(
    val imports: List<ImportDefinition>,
    val exports: List<ExportDefinition>,
)
