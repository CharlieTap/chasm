package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.NameData
import io.github.charlietap.chasm.fixture.runtime.type.externalType
import io.github.charlietap.chasm.runtime.type.ExternalType

fun exportDefinition(
    name: String = "",
    type: ExternalType = externalType(),
    nameData: NameData? = null,
) = ExportDefinition(
    name = name,
    type = type,
    nameData = nameData,
)
