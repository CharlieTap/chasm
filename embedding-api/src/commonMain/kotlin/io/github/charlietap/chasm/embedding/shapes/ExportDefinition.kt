package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType

data class ExportDefinition(
    val name: String,
    val index: ModuleIndex,
    val type: ExternalType,
    val nameData: NameData? = null,
)
