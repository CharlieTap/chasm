package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.Limits

fun limits(
    min: ULong = 0u,
    max: ULong? = null,
) = Limits(
    min = min,
    max = max,
)
