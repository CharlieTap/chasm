package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.decoder.section.ElementSection

internal fun elementSection(
    segments: List<ElementSegment> = emptyList(),
) = ElementSection(segments)
