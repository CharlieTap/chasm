package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.OperandCopyOrder
import io.github.charlietap.chasm.runtime.instruction.OperandCopyPlan
import kotlin.test.Test
import kotlin.test.assertEquals

class OperandCopyPlanTest {

    @Test
    fun `recognizes operands already in place`() {
        val plan = operandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(2), CopyOperand.Slot(3)),
            destinationSlotBase = 2,
        )

        assertEquals(OperandCopyOrder.None, plan.order)
    }

    @Test
    fun `plans forward copies when destinations do not overwrite later sources`() {
        val plan = operandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(1), CopyOperand.Slot(2)),
            destinationSlotBase = 0,
        )

        assertEquals(OperandCopyOrder.Forward, plan.order)
    }

    @Test
    fun `plans reverse copies when destinations would overwrite later sources`() {
        val plan = operandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(0), CopyOperand.Slot(1)),
            destinationSlotBase = 1,
        )

        assertEquals(OperandCopyOrder.Reverse, plan.order)
    }

    @Test
    fun `stages cyclic copies`() {
        val plan = operandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(1), CopyOperand.Slot(0)),
            destinationSlotBase = 0,
        )

        assertEquals(OperandCopyOrder.Staged, plan.order)
    }

    @Test
    fun `compares plans by operand contents`() {
        val first = OperandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(1), CopyOperand.Immediate(2)),
            order = OperandCopyOrder.Forward,
        )
        val second = OperandCopyPlan(
            operands = arrayOf(CopyOperand.Slot(1), CopyOperand.Immediate(2)),
            order = OperandCopyOrder.Forward,
        )

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}
