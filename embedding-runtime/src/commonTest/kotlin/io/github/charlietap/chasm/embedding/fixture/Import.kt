package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable

fun publicImport(
    moduleName: String = "",
    entityName: String = "",
    value: Importable = publicGlobal(),
) = Import(
    moduleName = moduleName,
    entityName = entityName,
    value = value,
)
