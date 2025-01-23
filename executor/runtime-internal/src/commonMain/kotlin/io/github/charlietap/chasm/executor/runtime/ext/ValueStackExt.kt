@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.jvm.JvmName

inline fun ValueStack.pushI32(i32: Int) {
    push(I32(i32))
}

inline fun ValueStack.pushI64(i64: Long) {
    push(I64(i64))
}

inline fun ValueStack.pushf32(f32: Float) {
    push(F32(f32))
}

inline fun ValueStack.pushf64(f64: Double) {
    push(F64(f64))
}

inline fun ValueStack.popI32(): Int {
    return try {
        (pop() as I32).value
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.I32ValueExpected)
    }
}

inline fun ValueStack.popI64(): Long {
    return try {
        (pop() as I64).value
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.I64ValueExpected)
    }
}

inline fun ValueStack.popF32(): Float {
    return try {
        (pop() as F32).value
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.F32ValueExpected)
    }
}

inline fun ValueStack.popF64(): Double {
    return try {
        (pop() as F64).value
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.F64ValueExpected)
    }
}

inline fun ValueStack.popReference(): ReferenceValue {
    return try {
        (pop() as ReferenceValue)
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.ReferenceValueExpected)
    }
}

inline fun ValueStack.peekReference(): ReferenceValue {
    return try {
        (peek() as ReferenceValue)
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.ReferenceValueExpected)
    }
}

inline fun ValueStack.popI31Reference(): UInt {
    return try {
        (pop() as ReferenceValue.I31).value
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.I31ReferenceExpected)
    }
}

inline fun ValueStack.popArrayReference(): ArrayInstance {
    return try {
        (pop() as ReferenceValue.Array).instance
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.ArrayReferenceExpected)
    }
}

inline fun ValueStack.popStructReference(): StructInstance {
    return try {
        (pop() as ReferenceValue.Struct).instance
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.StructReferenceExpected)
    }
}

inline fun ValueStack.popFunctionAddress(): Address.Function {
    return try {
        (pop() as ReferenceValue.Function).address
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.FunctionReferenceExpected)
    }
}

inline fun ValueStack.constOperation(
    const: Int,
) = push(I32(const))

inline fun ValueStack.constOperation(
    const: Long,
) = push(I64(const))

inline fun ValueStack.constOperation(
    const: Float,
) = push(F32(const))

inline fun ValueStack.constOperation(
    const: Double,
) = push(F64(const))

inline fun <S, T : NumberValue<S>> ValueStack.unaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S) -> S,
) {
    val operand = pop() as T

    val result = operation(operand.value)
    push(constructor(result))
}

@JvmName("i32UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Int) -> Int,
) = unaryOperation(::I32, operation)

@JvmName("i64UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Long) -> Long,
) = unaryOperation(::I64, operation)

@JvmName("f32UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Float) -> Float,
) = unaryOperation(::F32, operation)

@JvmName("f64UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Double) -> Double,
) = unaryOperation(::F64, operation)

inline fun <S, T : NumberValue<S>> ValueStack.binaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S, S) -> S,
) {
    val operand2 = pop() as T
    val operand1 = pop() as T

    val result = operation(operand1.value, operand2.value)
    push(constructor(result))
}

@JvmName("i32BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Int, Int) -> Int,
) = binaryOperation(::I32, operation)

@JvmName("i64BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Long, Long) -> Long,
) = binaryOperation(::I64, operation)

@JvmName("f32BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Float, Float) -> Float,
) = binaryOperation(::F32, operation)

@JvmName("f64BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Double, Double) -> Double,
) = binaryOperation(::F64, operation)

inline fun <S, T : NumberValue<S>> ValueStack.testOperation(
    crossinline operation: (S) -> Boolean,
) {
    val operand = pop() as T
    val result = if (operation(operand.value)) {
        I32(1)
    } else {
        I32(0)
    }
    push(result)
}

inline fun <S, T : NumberValue<S>> ValueStack.relationalOperation(
    crossinline operation: (S, S) -> Boolean,
) {
    val operand2 = pop() as T
    val operand1 = pop() as T
    val result = if (operation(operand1.value, operand2.value)) {
        I32(1)
    } else {
        I32(0)
    }
    push(result)
}

inline fun <S, A : NumberValue<S>, T, B : NumberValue<T>> ValueStack.convertOperation(
    crossinline constructor: (T) -> B,
    crossinline operation: (S) -> T,
) {
    val operand = pop() as A
    val result = try {
        operation(operand.value)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }
    push(constructor(result))
}
