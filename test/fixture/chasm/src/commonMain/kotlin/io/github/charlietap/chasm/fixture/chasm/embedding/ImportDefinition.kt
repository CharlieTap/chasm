package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.fixture.runtime.type.externalType
import io.github.charlietap.chasm.runtime.type.ExternalType

fun importDefinition(
    moduleName: String = "",
    entityName: String = "",
    type: ExternalType = externalType(),
) = ImportDefinition(
    moduleName = moduleName,
    entityName = entityName,
    type = type,
)
