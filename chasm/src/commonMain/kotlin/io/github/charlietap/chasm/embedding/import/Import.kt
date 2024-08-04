package io.github.charlietap.chasm.embedding.import

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

data class Import(
    val moduleName: String,
    val entityName: String,
    val value: ExternalValue,
)
