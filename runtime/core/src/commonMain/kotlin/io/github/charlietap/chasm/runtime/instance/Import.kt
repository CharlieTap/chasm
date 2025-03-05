package io.github.charlietap.chasm.runtime.instance

data class Import(
    val moduleName: String,
    val entityName: String,
    val externalValue: ExternalValue,
)
