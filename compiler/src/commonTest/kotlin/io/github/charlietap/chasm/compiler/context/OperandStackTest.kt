package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class OperandStackTest {

    @Test
    fun reusesPoppedOperandsAtTheSameStackDepth() {
        val stack = OperandStack()
        val first = stack.push(0)
        val second = stack.push(1)

        assertSame(second, stack.pop())
        assertSame(second, stack.push(2))
        assertEquals(listOf(first, second), stack.toList())
    }

    @Test
    fun excludesPooledOperandsAboveTheActiveStack() {
        val stack = OperandStack()
        stack.push(0)
        stack.push(1)
        stack.pop()

        assertEquals(1, stack.size)
        assertFailsWith<IndexOutOfBoundsException> {
            stack[1]
        }
    }

    @Test
    fun reusesOperandsAcrossStacksWithTheSamePool() {
        val pool = ArrayList<Operand>()
        val first = OperandStack(pool).push(0)
        val second = OperandStack(pool).push(0)

        assertSame(first, second)
    }

    @Test
    fun tracksTheHighestReservedSlotForEveryStackPrefix() {
        val stack = OperandStack()
        stack.push(3)
        stack.push(1)
        stack.push(5)

        assertEquals(-1, stack.highestReservedSlot(0))
        assertEquals(3, stack.highestReservedSlot(1))
        assertEquals(3, stack.highestReservedSlot(2))
        assertEquals(5, stack.highestReservedSlot())

        stack.pop()
        assertEquals(3, stack.highestReservedSlot())

        stack.push(2)
        assertEquals(3, stack.highestReservedSlot())
    }

    @Test
    fun tracksEveryUseOfAReservedSlot() {
        val stack = OperandStack()

        stack.push(5)
        stack.push(9)
        stack.push(5)

        assertTrue(stack.containsReservedSlot(5))
        assertTrue(stack.containsReservedSlot(9))
        assertFalse(stack.containsReservedSlot(4))

        stack.pop()
        assertTrue(stack.containsReservedSlot(5))

        stack.pop()
        stack.pop()
        assertFalse(stack.containsReservedSlot(5))
        assertFalse(stack.containsReservedSlot(9))
    }

    @Test
    fun tracksTheFirstUnmaterializedOperand() {
        val stack = OperandStack()
        stack.pushFrame(0)
        val first = stack.push(1)
        stack.pushFrame(2)
        val second = stack.push(3)

        assertEquals(1, stack.firstUnmaterializedIndex())

        second.materialize()
        stack.markMaterialized(second)
        assertEquals(1, stack.firstUnmaterializedIndex())

        first.materialize()
        stack.markMaterialized(first)
        assertEquals(-1, stack.firstUnmaterializedIndex())

        stack.pop()
        val reused = stack.push(4)
        assertSame(second, reused)
        assertEquals(3, stack.firstUnmaterializedIndex())

        stack.pop()
        assertEquals(-1, stack.firstUnmaterializedIndex())
    }
}

private fun OperandStack.push(reservedSlot: Int): Operand = push(
    type = null,
    reservedSlot = reservedSlot,
    sourceKind = OperandSourceKind.I32Immediate,
    sourceBits = reservedSlot.toLong(),
)

private fun OperandStack.pushFrame(reservedSlot: Int): Operand = push(
    type = null,
    reservedSlot = reservedSlot,
    sourceKind = OperandSourceKind.Frame,
    sourceBits = reservedSlot.toLong(),
)
