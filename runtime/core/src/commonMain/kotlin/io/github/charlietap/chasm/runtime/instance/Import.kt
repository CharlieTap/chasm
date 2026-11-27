package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class Import(
    val moduleName: String,
    val entityName: String,
    val externalValue: ExternalValue,
)
