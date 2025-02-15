package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.Limits

fun limits(
    min: UInt = 0u,
    max: UInt? = null,
) = Limits(
    min = min,
    max = max,
)
