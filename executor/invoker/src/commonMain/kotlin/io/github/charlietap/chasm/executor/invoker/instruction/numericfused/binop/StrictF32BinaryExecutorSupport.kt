package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeF32BinaryIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Float,
    right: Float,
    operation: (Float, Float) -> Float,
) {
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits().toLong())
}

internal inline fun executeF32BinaryIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Float,
    rightSlot: Int,
    operation: (Float, Float) -> Float,
) {
    val right = Float.fromBits(vstack.getFrameSlot(rightSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits().toLong())
}

internal inline fun executeF32BinarySi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Float,
    operation: (Float, Float) -> Float,
) {
    val left = Float.fromBits(vstack.getFrameSlot(leftSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits().toLong())
}

internal inline fun executeF32BinarySs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Float, Float) -> Float,
) {
    val left = Float.fromBits(vstack.getFrameSlot(leftSlot).toInt())
    val right = Float.fromBits(vstack.getFrameSlot(rightSlot).toInt())
    vstack.setFrameSlot(destinationSlot, operation(left, right).toRawBits().toLong())
}

internal inline fun executeF32RelopIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Float,
    right: Float,
    operation: (Float, Float) -> Boolean,
) {
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF32RelopIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Float,
    rightSlot: Int,
    operation: (Float, Float) -> Boolean,
) {
    val right = Float.fromBits(vstack.getFrameSlot(rightSlot).toInt())
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF32RelopSi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Float,
    operation: (Float, Float) -> Boolean,
) {
    val left = Float.fromBits(vstack.getFrameSlot(leftSlot).toInt())
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}

internal inline fun executeF32RelopSs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Float, Float) -> Boolean,
) {
    val left = Float.fromBits(vstack.getFrameSlot(leftSlot).toInt())
    val right = Float.fromBits(vstack.getFrameSlot(rightSlot).toInt())
    vstack.setFrameSlot(destinationSlot, if (operation(left, right)) 1L else 0L)
}
