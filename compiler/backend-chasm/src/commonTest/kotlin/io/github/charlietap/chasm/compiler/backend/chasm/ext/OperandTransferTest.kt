package io.github.charlietap.chasm.compiler.backend.chasm.ext

import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.TransferSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OperandTransferTest {

    @Test
    fun `recognizes operands already in place`() {
        val transfer = OperandTransfer(
            sources = listOf(TransferSource.Slot(2), TransferSource.Slot(3)),
            destinationSlotBase = 2,
        ).toRuntime()

        assertTrue(transfer.isInPlace)
    }

    @Test
    fun `selects forward transfer when destinations do not overwrite later sources`() {
        val transfer = OperandTransfer(
            sources = listOf(TransferSource.Slot(1), TransferSource.Slot(2)),
            destinationSlotBase = 0,
        ).toRuntime()

        assertEquals(2, transfer.operationCount)
        assertEquals(0, transfer.scratchSaveCount)
    }

    @Test
    fun `selects reverse transfer when destinations would overwrite later sources`() {
        val transfer = OperandTransfer(
            sources = listOf(TransferSource.Slot(0), TransferSource.Slot(1)),
            destinationSlotBase = 1,
        ).toRuntime()

        assertEquals(2, transfer.operationCount)
        assertEquals(0, transfer.scratchSaveCount)
    }

    @Test
    fun `stages cyclic transfers`() {
        val transfer = OperandTransfer(
            sources = listOf(TransferSource.Slot(1), TransferSource.Slot(0)),
            destinationSlotBase = 0,
        ).toRuntime()

        assertEquals(3, transfer.operationCount)
        assertEquals(1, transfer.scratchSaveCount)
    }

    @Test
    fun `compares transfers by source contents`() {
        val first = OperandTransfer(
            sources = listOf(TransferSource.Slot(1), TransferSource.Immediate(2)),
            destinationSlotBase = 0,
        ).toRuntime()
        val second = OperandTransfer(
            sources = listOf(TransferSource.Slot(1), TransferSource.Immediate(2)),
            destinationSlotBase = 0,
        ).toRuntime()

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}
