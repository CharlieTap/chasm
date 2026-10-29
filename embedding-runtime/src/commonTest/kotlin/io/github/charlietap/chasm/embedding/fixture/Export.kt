package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Importable

fun publicExport(
    name: String = "",
    value: Importable = publicGlobal(),
) = Export(
    name = name,
    value = value,
)
