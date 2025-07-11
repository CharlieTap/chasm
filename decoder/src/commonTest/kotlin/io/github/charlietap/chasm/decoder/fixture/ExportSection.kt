package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.decoder.section.ExportSection

internal fun exportSection(
    exports: List<Export> = emptyList(),
) = ExportSection(exports)
