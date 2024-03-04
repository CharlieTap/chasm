package io.github.charlietap.chasm.import

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

data class Import(
    val moduleName: String,
    val entityName: String,
    val value: ExternalValue,
)
