package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.value.NameValue

data class ExportInstance(
    val name: NameValue,
    val value: ExternalValue,
)
