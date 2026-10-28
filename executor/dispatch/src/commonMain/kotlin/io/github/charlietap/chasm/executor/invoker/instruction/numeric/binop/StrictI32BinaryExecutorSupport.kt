package io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI32BinaryIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Int,
    right: Int,
    operation: (Int, Int) -> Int,
) {
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
}

internal inline fun executeI32BinaryIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Int,
    rightSlot: Int,
    operation: (Int, Int) -> Int,
) {
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
}

internal inline fun executeI32BinarySi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Int,
    operation: (Int, Int) -> Int,
) {
    val left = vstack.getFrameSlot(leftSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
}

internal inline fun executeI32BinarySs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Int, Int) -> Int,
) {
    val left = vstack.getFrameSlot(leftSlot).toInt()
    val right = vstack.getFrameSlot(rightSlot).toInt()
    vstack.setFrameSlot(destinationSlot, operation(left, right).toLong())
}
