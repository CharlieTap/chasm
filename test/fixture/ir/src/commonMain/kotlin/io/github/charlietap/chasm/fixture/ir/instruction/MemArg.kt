package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.MemArg

fun memArg(
    align: UInt = 0u,
    offset: UInt = 0u,
) = MemArg(
    align = align,
    offset = offset,
)
