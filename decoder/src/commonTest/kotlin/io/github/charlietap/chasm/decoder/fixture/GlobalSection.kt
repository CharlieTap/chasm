package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.decoder.section.GlobalSection

internal fun globalSection(
    globals: List<Global> = emptyList(),
) = GlobalSection(globals)
