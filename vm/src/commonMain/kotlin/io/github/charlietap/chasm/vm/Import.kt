package io.github.charlietap.chasm.vm

data class Import(
    val moduleName: String,
    val entityName: String,
    val address: ExternalAddress,
)
