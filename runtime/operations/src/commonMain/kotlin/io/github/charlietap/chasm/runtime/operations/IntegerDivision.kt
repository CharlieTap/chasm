package io.github.charlietap.chasm.runtime.operations

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun strictI32DivS(
    left: Int,
    right: Int,
): Int {
    if (left == Int.MIN_VALUE && right == -1) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    return try {
        left / right
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}

fun strictI32DivU(
    left: Int,
    right: Int,
): Int = try {
    (left.toUInt() / right.toUInt()).toInt()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

fun strictI32RemS(
    left: Int,
    right: Int,
): Int = try {
    left % right
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

fun strictI32RemU(
    left: Int,
    right: Int,
): Int = try {
    (left.toUInt() % right.toUInt()).toInt()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

fun strictI64DivS(
    left: Long,
    right: Long,
): Long {
    if (left == Long.MIN_VALUE && right == -1L) {
        throw InvocationException(InvocationError.IntegerOverflow)
    }

    return try {
        left / right
    } catch (_: ArithmeticException) {
        throw InvocationException(InvocationError.CannotDivideIntegerByZero)
    }
}

fun strictI64DivU(
    left: Long,
    right: Long,
): Long = try {
    (left.toULong() / right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

fun strictI64RemS(
    left: Long,
    right: Long,
): Long = try {
    left % right
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}

fun strictI64RemU(
    left: Long,
    right: Long,
): Long = try {
    (left.toULong() % right.toULong()).toLong()
} catch (_: ArithmeticException) {
    throw InvocationException(InvocationError.CannotDivideIntegerByZero)
}
