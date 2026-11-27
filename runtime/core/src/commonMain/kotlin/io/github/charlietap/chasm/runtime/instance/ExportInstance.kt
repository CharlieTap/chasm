package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.value.NameValue

@InternalChasmApi
data class ExportInstance(
    val name: NameValue,
    val value: ExternalValue,
)
