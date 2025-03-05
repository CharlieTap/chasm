package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.MemArg

fun runtimeMemArg(
    align: Int = 0,
    offset: Int = 0,
) = MemArg(
    align = align,
    offset = offset,
)
