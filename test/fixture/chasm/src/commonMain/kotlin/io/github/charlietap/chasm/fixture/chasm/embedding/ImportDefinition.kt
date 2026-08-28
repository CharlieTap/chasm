package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.fixture.host.functionIndex
import io.github.charlietap.chasm.fixture.runtime.type.externalType
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.runtime.type.ExternalType

fun importDefinition(
    moduleName: String = "",
    entityName: String = "",
    index: ModuleIndex = functionIndex(),
    type: ExternalType = externalType(),
) = ImportDefinition(
    moduleName = moduleName,
    entityName = entityName,
    index = index,
    type = type,
)
