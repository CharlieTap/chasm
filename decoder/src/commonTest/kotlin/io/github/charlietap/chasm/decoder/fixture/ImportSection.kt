package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.decoder.section.ImportSection

internal fun importSection(
    imports: List<Import> = emptyList(),
) = ImportSection(imports)
