package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.program.EXIT_IP
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.stack.activationCallerFrameDelta
import io.github.charlietap.chasm.runtime.stack.activationHeader
import io.github.charlietap.chasm.runtime.stack.activationReturnIp
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StackTest {

    @Test
    fun `exact operand schedule omits identities and orders acyclic moves`() {
        val stack = vstack().apply {
            reserveDepth(4)
            setFrameSlot(0, 10)
            setFrameSlot(1, 11)
            setFrameSlot(2, 12)
        }
        val transfer = OperandTransfer(
            arrayOf(TransferSource.Slot(2), TransferSource.Slot(0), TransferSource.Immediate(99)),
            destinationSlotBase = 0,
        )

        stack.transferOperands(currentFp = 0, destinationFp = 0, transfer = transfer)

        assertEquals(0, transfer.scratchSaveCount)
        assertEquals(12, stack.getFrameSlot(0))
        assertEquals(10, stack.getFrameSlot(1))
        assertEquals(99, stack.getFrameSlot(2))
    }

    @Test
    fun `exact operand schedule uses one scalar save for each actual cycle`() {
        val stack = vstack().apply {
            reserveDepth(4)
            setFrameSlot(0, 10)
            setFrameSlot(1, 11)
            setFrameSlot(2, 12)
            setFrameSlot(3, 13)
        }
        val transfer = OperandTransfer(
            arrayOf(
                TransferSource.Slot(1),
                TransferSource.Slot(0),
                TransferSource.Slot(3),
                TransferSource.Slot(2),
            ),
            destinationSlotBase = 0,
        )

        stack.transferOperands(currentFp = 0, destinationFp = 0, transfer = transfer)

        assertEquals(2, transfer.scratchSaveCount)
        assertEquals(11, stack.getFrameSlot(0))
        assertEquals(10, stack.getFrameSlot(1))
        assertEquals(13, stack.getFrameSlot(2))
        assertEquals(12, stack.getFrameSlot(3))
    }

    @Test
    fun `exact operand schedules preserve every overlapping slot mapping through arity five`() {
        for (size in 1..5) {
            for (destinationSlotBase in 0..1) {
                val sourceLimit = size + destinationSlotBase
                val sourceSlots = IntArray(size)

                fun verify(index: Int) {
                    if (index < size) {
                        for (sourceSlot in 0 until sourceLimit) {
                            sourceSlots[index] = sourceSlot
                            verify(index + 1)
                        }
                        return
                    }

                    val original = LongArray(sourceLimit) { slot -> 100L + slot }
                    val stack = vstack().apply {
                        reserveDepth(sourceLimit + size)
                        for (slot in original.indices) setFrameSlot(slot, original[slot])
                    }
                    val transfer = OperandTransfer(
                        Array(size) { operand -> TransferSource.Slot(sourceSlots[operand]) },
                        destinationSlotBase,
                    )

                    stack.transferOperands(0, destinationSlotBase, transfer)

                    val expected = LongArray(size) { operand -> original[sourceSlots[operand]] }
                    val actual = LongArray(size) { operand -> stack.getFrameSlot(destinationSlotBase + operand) }
                    assertContentEquals(expected, actual)
                }

                verify(0)
            }
        }
    }

    @Test
    fun `activation header preserves its fields without looking like a reference`() {
        val callerFrameDelta = (1 shl 25) - 1
        val returnIp = Int.MAX_VALUE - 1

        val header = activationHeader(returnIp, callerFrameDelta)

        assertEquals(0L, header and 0xffL)
        assertEquals(callerFrameDelta, activationCallerFrameDelta(header))
        assertEquals(returnIp, activationReturnIp(header))
    }

    @Test
    fun `activation header reserves its highest return code for exit`() {
        val header = activationHeader(EXIT_IP, 0)

        assertEquals(0L, header and 0xffL)
        assertEquals(0, activationCallerFrameDelta(header))
        assertEquals(EXIT_IP, activationReturnIp(header))
    }

    @Test
    fun `activation header restores the caller and keeps results live`() {
        val stack = vstack()
        stack.reserveDepth(9)
        stack.fp = 2
        stack.writeActivationHeader(
            calleeFp = 6,
            activationHeaderSlot = 2,
            activationHeader = activationHeader(37, 4),
        )
        stack.activateFrame(fp = 6, frameSlots = 3)
        stack.setFrameSlot(0, 41)
        stack.setFrameSlot(1, 42)

        val returnIp = stack.restoreCallerFrame(resultCount = 2, activationHeaderSlot = 2)

        assertEquals(37, returnIp)
        assertEquals(2, stack.fp)
        assertEquals(8, stack.sp)
        assertEquals(41, stack.getFrameSlot(6, 0))
        assertEquals(42, stack.getFrameSlot(6, 1))
    }

    @Test
    fun `root activation header contains the exit sentinel`() {
        val stack = vstack()
        stack.reserveDepth(1)
        stack.writeRootActivationHeader(activationHeaderSlot = 0)
        stack.activateFrame(fp = 0, frameSlots = 1)

        val returnIp = stack.restoreCallerFrame(resultCount = 0, activationHeaderSlot = 0)

        assertEquals(EXIT_IP, returnIp)
        assertEquals(0, stack.fp)
        assertEquals(0, stack.sp)
    }

    @Test
    fun `frame deltas restore a deep chain of activations`() {
        val depth = 10_000
        val stack = ValueStack()
        stack.reserveDepth(1)
        stack.writeRootActivationHeader(activationHeaderSlot = 0)
        stack.activateFrame(fp = 0, frameSlots = 1)

        repeat(depth) { returnIp ->
            stack.activateLinkedFrame(
                callFrameOffset = 1,
                frameEndOffset = 2,
                activationHeaderSlot = 0,
                activationHeader = activationHeader(returnIp, callerFrameDelta = 1),
            )
        }

        repeat(depth) { index ->
            assertEquals(depth - index - 1, stack.restoreCallerFrame(resultCount = 0, activationHeaderSlot = 0))
        }
        assertEquals(0, stack.fp)
    }

    @OptIn(UnsafeHostApi::class)
    @Test
    fun `large capacity request grows directly to its final power of two`() {
        val stack = ValueStack(minCapacity = 32)

        stack.ensureCapacity((1 shl 20) - 1)

        assertEquals(1 shl 20, stack.unsafeElements().size)
    }

    @Test
    fun `linked three-slot frame activation stages overlapping operands`() {
        val stack = vstack()
        stack.reserveDepth(4)
        stack.setFrameSlot(0, 10)
        stack.setFrameSlot(1, 11)
        stack.setFrameSlot(2, 12)

        stack.activateLinkedFrameWithThreeSlots(
            callFrameOffset = 2,
            frameEndOffset = 6,
            activationHeaderSlot = 3,
            activationHeader = activationHeader(37, 2),
            firstSourceSlot = 2,
            secondSourceSlot = 0,
            thirdSourceSlot = 1,
        )

        assertEquals(2, stack.fp)
        assertEquals(6, stack.sp)
        assertEquals(12, stack.getFrameSlot(0))
        assertEquals(10, stack.getFrameSlot(1))
        assertEquals(11, stack.getFrameSlot(2))
    }

    @Test
    fun `linked four-slot frame activation stages overlapping operands`() {
        val stack = vstack()
        stack.reserveDepth(4)
        stack.setFrameSlot(0, 10)
        stack.setFrameSlot(1, 11)
        stack.setFrameSlot(2, 12)
        stack.setFrameSlot(3, 13)

        stack.activateLinkedFrameWithFourSlots(
            callFrameOffset = 2,
            frameEndOffset = 7,
            activationHeaderSlot = 4,
            activationHeader = activationHeader(37, 2),
            firstSourceSlot = 3,
            secondSourceSlot = 2,
            thirdSourceSlot = 1,
            fourthSourceSlot = 0,
        )

        assertEquals(2, stack.fp)
        assertEquals(7, stack.sp)
        assertEquals(13, stack.getFrameSlot(0))
        assertEquals(12, stack.getFrameSlot(1))
        assertEquals(11, stack.getFrameSlot(2))
        assertEquals(10, stack.getFrameSlot(3))
    }

    @Test
    fun `peek nth value returns the correct value`() {

        val stack = vstack()

        val value1 = 1
        val value2 = 2

        stack.pushI32(value1)
        stack.pushI32(value2)

        val result1 = stack.peekNthI32(0)
        val result2 = stack.peekNthI32(1)

        assertEquals(value2, result1)
        assertEquals(value1, result2)
    }

    @Test
    fun `squash n returns the correct value`() {

        val stack = vstack()

        val value1 = 0
        val value2 = 1
        val value3 = 2
        val value4 = 3
        val value5 = 4

        stack.pushI32(value1)
        stack.pushI32(value2)
        stack.pushI32(value3)
        stack.pushI32(value4)
        stack.pushI32(value5)

        stack.shrink(2, 1)

        val expected = listOf(value1, value4, value5)
        val actual = List(stack.sp) {
            stack.popI32()
        }.asReversed()

        assertEquals(expected, actual)
    }
}
