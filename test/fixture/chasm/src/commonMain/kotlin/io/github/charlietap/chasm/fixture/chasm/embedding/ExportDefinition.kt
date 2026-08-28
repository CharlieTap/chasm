package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.NameData
import io.github.charlietap.chasm.fixture.host.functionIndex
import io.github.charlietap.chasm.fixture.runtime.type.externalType
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType

fun exportDefinition(
    name: String = "",
    index: ModuleIndex = functionIndex(),
    type: ExternalType = externalType(),
    nameData: NameData? = null,
) = ExportDefinition(
    name = name,
    index = index,
    type = type,
    nameData = nameData,
)
