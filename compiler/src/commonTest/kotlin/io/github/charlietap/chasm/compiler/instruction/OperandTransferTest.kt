package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OperandTransferTest {

    @Test
    fun `recognizes operands already in place`() {
        val transfer = selectOperandTransfer(
            sources = arrayOf(TransferSource.Slot(2), TransferSource.Slot(3)),
            destinationSlotBase = 2,
        )

        assertTrue(transfer.isInPlace)
    }

    @Test
    fun `selects forward transfer when destinations do not overwrite later sources`() {
        val transfer = selectOperandTransfer(
            sources = arrayOf(TransferSource.Slot(1), TransferSource.Slot(2)),
            destinationSlotBase = 0,
        )

        assertEquals(2, transfer.operationCount)
        assertEquals(0, transfer.scratchSaveCount)
    }

    @Test
    fun `selects reverse transfer when destinations would overwrite later sources`() {
        val transfer = selectOperandTransfer(
            sources = arrayOf(TransferSource.Slot(0), TransferSource.Slot(1)),
            destinationSlotBase = 1,
        )

        assertEquals(2, transfer.operationCount)
        assertEquals(0, transfer.scratchSaveCount)
    }

    @Test
    fun `stages cyclic transfers`() {
        val transfer = selectOperandTransfer(
            sources = arrayOf(TransferSource.Slot(1), TransferSource.Slot(0)),
            destinationSlotBase = 0,
        )

        assertEquals(3, transfer.operationCount)
        assertEquals(1, transfer.scratchSaveCount)
    }

    @Test
    fun `compares transfers by source contents`() {
        val first = OperandTransfer(
            sources = arrayOf(TransferSource.Slot(1), TransferSource.Immediate(2)),
            destinationSlotBase = 0,
        )
        val second = OperandTransfer(
            sources = arrayOf(TransferSource.Slot(1), TransferSource.Immediate(2)),
            destinationSlotBase = 0,
        )

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}
