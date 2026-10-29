package io.github.charlietap.chasm.embedding.shapes

data class Import(
    val moduleName: String,
    val entityName: String,
    val value: Importable,
)
