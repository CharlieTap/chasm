package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource

fun operandTransfer(
    sources: Array<TransferSource> = emptyArray(),
    destinationSlotBase: Int = 0,
) = OperandTransfer(
    sources = sources,
    destinationSlotBase = destinationSlotBase,
)
