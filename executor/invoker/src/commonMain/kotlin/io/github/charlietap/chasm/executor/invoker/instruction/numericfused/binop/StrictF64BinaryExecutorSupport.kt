package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF64BinaryIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Double,
    right: Double,
    operation: (Double, Double) -> Double,
) {
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits())
}

internal inline fun executeF64BinaryIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Double,
    rightSlot: Int,
    operation: (Double, Double) -> Double,
) {
    val right = Double.fromBits(vstack.getFrameSlot(rightSlot))
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits())
}

internal inline fun executeF64BinarySi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Double,
    operation: (Double, Double) -> Double,
) {
    val left = Double.fromBits(vstack.getFrameSlot(leftSlot))
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits())
}

internal inline fun executeF64BinarySs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Double, Double) -> Double,
) {
    val left = Double.fromBits(vstack.getFrameSlot(leftSlot))
    val right = Double.fromBits(vstack.getFrameSlot(rightSlot))
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits())
}

internal inline fun executeF64RelopIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Double,
    right: Double,
    operation: (Double, Double) -> Boolean,
) {
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF64RelopIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Double,
    rightSlot: Int,
    operation: (Double, Double) -> Boolean,
) {
    val right = Double.fromBits(vstack.getFrameSlot(rightSlot))
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF64RelopSi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Double,
    operation: (Double, Double) -> Boolean,
) {
    val left = Double.fromBits(vstack.getFrameSlot(leftSlot))
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF64RelopSs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Double, Double) -> Boolean,
) {
    val left = Double.fromBits(vstack.getFrameSlot(leftSlot))
    val right = Double.fromBits(vstack.getFrameSlot(rightSlot))
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}
