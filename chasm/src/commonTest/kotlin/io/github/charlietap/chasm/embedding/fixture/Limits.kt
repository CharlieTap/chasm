package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Limits

fun publicLimits(
    min: UInt = 0u,
    max: UInt? = null,
): Limits = Limits(min, max)
