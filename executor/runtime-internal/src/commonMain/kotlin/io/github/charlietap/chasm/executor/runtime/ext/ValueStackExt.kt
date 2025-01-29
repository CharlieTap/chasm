@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.jvm.JvmName

inline fun ValueStack.pushExecution(
    value: ExecutionValue,
) {
    push(value.toLong())
}

inline fun ValueStack.pushReference(
    value: ReferenceValue,
) {
    push(value.toLong())
}

inline fun ValueStack.popReference(): ReferenceValue {
    return try {
        (pop().toReferenceValue())
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.ReferenceValueExpected)
    }
}

inline fun ValueStack.peekReference(): ReferenceValue {
    return try {
        (peek().toReferenceValue())
    } catch (_: ClassCastException) {
        throw InvocationException(InvocationError.ReferenceValueExpected)
    }
}

inline fun ValueStack.popI31(): UInt = pop().toI31()

inline fun ValueStack.popArrayAddress(): Address.Array = pop().toArrayAddress()

inline fun ValueStack.popStructAddress(): Address.Struct = pop().toStructAddress()

inline fun ValueStack.popFunctionAddress(): Address.Function = pop().toFunctionAddress()

inline fun ValueStack.constOperation(
    const: Int,
) = pushI32(const)

inline fun ValueStack.constOperation(
    const: Long,
) = pushI64(const)

inline fun ValueStack.constOperation(
    const: Float,
) = pushF32(const)

inline fun ValueStack.constOperation(
    const: Double,
) = pushF64(const)

@JvmName("i32UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Int) -> Int,
) {
    val operand = popI32()

    val result = operation(operand)
    pushI32(result)
}

@JvmName("i64UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Long) -> Long,
) {
    val operand = popI64()

    val result = operation(operand)
    pushI64(result)
}

@JvmName("f32UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Float) -> Float,
) {
    val operand = popF32()

    val result = operation(operand)
    pushF32(result)
}

@JvmName("f64UnaryOperation")
inline fun ValueStack.unaryOperation(
    crossinline operation: (Double) -> Double,
) {
    val operand = popF64()

    val result = operation(operand)
    pushF64(result)
}

@JvmName("i32BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Int, Int) -> Int,
) {
    val operand2 = popI32()
    val operand1 = popI32()

    val result = operation(operand1, operand2)
    pushI32(result)
}

@JvmName("i64BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Long, Long) -> Long,
) {
    val operand2 = popI64()
    val operand1 = popI64()

    val result = operation(operand1, operand2)
    pushI64(result)
}

@JvmName("f32BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Float, Float) -> Float,
) {
    val operand2 = popF32()
    val operand1 = popF32()

    val result = operation(operand1, operand2)
    pushF32(result)
}

@JvmName("f64BinaryOperation")
inline fun ValueStack.binaryOperation(
    crossinline operation: (Double, Double) -> Double,
) {
    val operand2 = popF64()
    val operand1 = popF64()

    val result = operation(operand1, operand2)
    pushF64(result)
}

@JvmName("i32TestOperation")
inline fun ValueStack.testOperation(
    crossinline operation: (Int) -> Boolean,
) {
    val operand = popI32()
    val result = if (operation(operand)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("i64TestOperation")
inline fun ValueStack.testOperation(
    crossinline operation: (Long) -> Boolean,
) {
    val operand = popI64()
    val result = if (operation(operand)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("f32TestOperation")
inline fun ValueStack.testOperation(
    crossinline operation: (Float) -> Boolean,
) {
    val operand = popF32()
    val result = if (operation(operand)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("f64TestOperation")
inline fun ValueStack.testOperation(
    crossinline operation: (Double) -> Boolean,
) {
    val operand = popF64()
    val result = if (operation(operand)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("i32RelationalOperation")
inline fun ValueStack.relationalOperation(
    crossinline operation: (Int, Int) -> Boolean,
) {
    val operand2 = popI32()
    val operand1 = popI32()
    val result = if (operation(operand1, operand2)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("i64RelationalOperation")
inline fun ValueStack.relationalOperation(
    crossinline operation: (Long, Long) -> Boolean,
) {
    val operand2 = popI64()
    val operand1 = popI64()
    val result = if (operation(operand1, operand2)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("f32RelationalOperation")
inline fun ValueStack.relationalOperation(
    crossinline operation: (Float, Float) -> Boolean,
) {
    val operand2 = popF32()
    val operand1 = popF32()
    val result = if (operation(operand1, operand2)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("f64RelationalOperation")
inline fun ValueStack.relationalOperation(
    crossinline operation: (Double, Double) -> Boolean,
) {
    val operand2 = popF64()
    val operand1 = popF64()
    val result = if (operation(operand1, operand2)) {
        1L
    } else {
        0L
    }
    push(result)
}

@JvmName("i32i64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Int) -> Long,
) {
    val operand = popI32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI64(result)
}

@JvmName("i32f32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Int) -> Float,
) {
    val operand = popI32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF32(result)
}

@JvmName("i32f64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Int) -> Double,
) {
    val operand = popI32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF64(result)
}

@JvmName("i64i32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Long) -> Int,
) {
    val operand = popI64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI32(result)
}

@JvmName("i64f32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Long) -> Float,
) {
    val operand = popI64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF32(result)
}

@JvmName("i64f64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Long) -> Double,
) {
    val operand = popI64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF64(result)
}

@JvmName("f32i32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Float) -> Int,
) {
    val operand = popF32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI32(result)
}

@JvmName("f32i64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Float) -> Long,
) {
    val operand = popF32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI64(result)
}

@JvmName("f32f64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Float) -> Double,
) {
    val operand = popF32()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF64(result)
}

@JvmName("f64i32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Double) -> Int,
) {
    val operand = popF64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI32(result)
}

@JvmName("f64i64ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Double) -> Long,
) {
    val operand = popF64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushI64(result)
}

@JvmName("f64f32ConvertOperation")
inline fun ValueStack.convertOperation(
    crossinline operation: (Double) -> Float,
) {
    val operand = popF64()
    val result = try {
        operation(operand)
    } catch (_: Throwable) {
        throw InvocationException(InvocationError.ConvertOperationFailed)
    }
    pushF32(result)
}
