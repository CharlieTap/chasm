package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun executeI64BinaryIi(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Long,
    right: Long,
    operation: (Long, Long) -> Long,
) {
    vstack.setFrameSlot(destinationSlot, operation(left, right))
}

internal inline fun executeI64BinaryIs(
    vstack: ValueStack,
    destinationSlot: Int,
    left: Long,
    rightSlot: Int,
    operation: (Long, Long) -> Long,
) {
    val right = vstack.getFrameSlot(rightSlot)
    vstack.setFrameSlot(destinationSlot, operation(left, right))
}

internal inline fun executeI64BinarySi(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    right: Long,
    operation: (Long, Long) -> Long,
) {
    val left = vstack.getFrameSlot(leftSlot)
    vstack.setFrameSlot(destinationSlot, operation(left, right))
}

internal inline fun executeI64BinarySs(
    vstack: ValueStack,
    destinationSlot: Int,
    leftSlot: Int,
    rightSlot: Int,
    operation: (Long, Long) -> Long,
) {
    val left = vstack.getFrameSlot(leftSlot)
    val right = vstack.getFrameSlot(rightSlot)
    vstack.setFrameSlot(destinationSlot, operation(left, right))
}

internal fun strictI64DivS(left: Long, right: Long): Long {
    if (left == Long.MIN_VALUE && right == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    return try {
        left / right
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}

internal fun strictI64DivU(left: Long, right: Long): Long = try {
    (left.toULong() / right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

internal fun strictI64RemS(left: Long, right: Long): Long = try {
    left % right
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

internal fun strictI64RemU(left: Long, right: Long): Long = try {
    (left.toULong() % right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}
