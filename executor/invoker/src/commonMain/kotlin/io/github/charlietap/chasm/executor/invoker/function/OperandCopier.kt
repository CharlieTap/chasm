package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.OperandCopyOrder
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun copyOperands(
    vstack: ValueStack,
    currentFramePointer: Int,
    destinationFramePointer: Int,
    operands: Array<CopyOperand>,
    order: OperandCopyOrder,
) {
    when (order) {
        OperandCopyOrder.None -> Unit
        OperandCopyOrder.Forward -> {
            var index = 0
            while (index < operands.size) {
                copyOperand(vstack, currentFramePointer, destinationFramePointer, operands, index)
                index++
            }
        }
        OperandCopyOrder.Reverse -> {
            var index = operands.lastIndex
            while (index >= 0) {
                copyOperand(vstack, currentFramePointer, destinationFramePointer, operands, index)
                index--
            }
        }
        OperandCopyOrder.Staged -> {
            val values = LongArray(operands.size) { index ->
                operandValue(vstack, currentFramePointer, operands[index])
            }
            var index = 0
            while (index < values.size) {
                vstack.setFrameSlot(destinationFramePointer, index, values[index])
                index++
            }
        }
    }
}

internal fun copyOperands(
    vstack: ValueStack,
    currentFramePointer: Int,
    destinationFramePointer: Int,
    operands: List<CopyOperand>,
    order: OperandCopyOrder,
) {
    when (order) {
        OperandCopyOrder.None -> Unit
        OperandCopyOrder.Forward -> {
            var index = 0
            while (index < operands.size) {
                val value = operandValue(vstack, currentFramePointer, operands[index])
                vstack.setFrameSlot(destinationFramePointer, index, value)
                index++
            }
        }
        OperandCopyOrder.Reverse -> {
            var index = operands.lastIndex
            while (index >= 0) {
                val value = operandValue(vstack, currentFramePointer, operands[index])
                vstack.setFrameSlot(destinationFramePointer, index, value)
                index--
            }
        }
        OperandCopyOrder.Staged -> {
            val values = LongArray(operands.size) { index ->
                operandValue(vstack, currentFramePointer, operands[index])
            }
            var index = 0
            while (index < values.size) {
                vstack.setFrameSlot(destinationFramePointer, index, values[index])
                index++
            }
        }
    }
}

internal fun operandCopyOrder(
    currentFramePointer: Int,
    destinationFramePointer: Int,
    operands: List<CopyOperand>,
): OperandCopyOrder {
    var index = 0
    while (index < operands.size) {
        val operand = operands[index]
        if (
            operand !is CopyOperand.Slot ||
            currentFramePointer + operand.slot != destinationFramePointer + index
        ) {
            break
        }
        index++
    }
    if (index == operands.size) return OperandCopyOrder.None

    index = 0
    while (index < operands.size) {
        val destination = destinationFramePointer + index
        var remainingIndex = index + 1
        while (remainingIndex < operands.size) {
            if (operands[remainingIndex].readsSlot(currentFramePointer, destination)) break
            remainingIndex++
        }
        if (remainingIndex < operands.size) break
        index++
    }
    if (index == operands.size) return OperandCopyOrder.Forward

    index = operands.lastIndex
    while (index >= 0) {
        val destination = destinationFramePointer + index
        var remainingIndex = index - 1
        while (remainingIndex >= 0) {
            if (operands[remainingIndex].readsSlot(currentFramePointer, destination)) break
            remainingIndex--
        }
        if (remainingIndex >= 0) break
        index--
    }
    return if (index < 0) OperandCopyOrder.Reverse else OperandCopyOrder.Staged
}

internal fun operandValue(
    vstack: ValueStack,
    framePointer: Int,
    operand: CopyOperand,
): Long = when (operand) {
    is CopyOperand.Immediate -> operand.value
    is CopyOperand.Slot -> vstack.getFrameSlot(framePointer, operand.slot)
}

private fun copyOperand(
    vstack: ValueStack,
    currentFramePointer: Int,
    destinationFramePointer: Int,
    operands: Array<CopyOperand>,
    index: Int,
) {
    val value = operandValue(vstack, currentFramePointer, operands[index])
    vstack.setFrameSlot(destinationFramePointer, index, value)
}

private fun CopyOperand.readsSlot(framePointer: Int, slot: Int): Boolean =
    this is CopyOperand.Slot && framePointer + this.slot == slot
