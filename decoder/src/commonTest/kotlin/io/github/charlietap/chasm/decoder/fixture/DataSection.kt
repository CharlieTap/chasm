package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.decoder.section.DataSection

internal fun dataSection(
    segments: List<DataSegment> = emptyList(),
) = DataSection(segments)
