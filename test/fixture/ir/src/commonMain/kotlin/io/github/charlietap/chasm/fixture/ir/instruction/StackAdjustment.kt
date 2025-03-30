package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.ir.instruction.StackAdjustment

fun stackAdjustment(
    encoded: Long = 0L,
) = StackAdjustment(
    encoded = encoded,
)

fun stackAdjustment(
    depth: Int = 0,
    keep: Int = 0,
) = StackAdjustment(
    depth = depth,
    keep = keep,
)
