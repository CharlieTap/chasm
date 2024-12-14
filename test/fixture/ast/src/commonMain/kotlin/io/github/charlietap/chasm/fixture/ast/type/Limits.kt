package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.Limits

fun limits(
    min: UInt = 0u,
    max: UInt? = null,
) = Limits(
    min = min,
    max = max,
)
