package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.Limits

fun limits(
    min: UInt = 0u,
    max: UInt? = null,
) = Limits(
    min = min,
    max = max,
)
