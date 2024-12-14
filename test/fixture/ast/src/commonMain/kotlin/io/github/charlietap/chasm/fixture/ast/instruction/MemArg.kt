package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.MemArg

fun memArg(
    align: UInt = 0u,
    offset: UInt = 0u,
) = MemArg(
    align = align,
    offset = offset,
)
