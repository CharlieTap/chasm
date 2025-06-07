package io.github.charlietap.chasm.fixture.chasm.embedding

import io.github.charlietap.chasm.embedding.shapes.ExportDefinition
import io.github.charlietap.chasm.embedding.shapes.ImportDefinition
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo

fun moduleInfo(
    imports: List<ImportDefinition> = emptyList(),
    exports: List<ExportDefinition> = emptyList(),
) = ModuleInfo(
    imports = imports,
    exports = exports,
)
