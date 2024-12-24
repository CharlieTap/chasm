@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.runtime.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import kotlin.jvm.JvmName

inline fun Stack.pushI32(i32: Int) {
    push(I32(i32))
}

inline fun Stack.pushI64(i64: Long) {
    push(I64(i64))
}

inline fun Stack.pushf32(f32: Float) {
    push(F32(f32))
}

inline fun Stack.pushf64(f64: Double) {
    push(F64(f64))
}

inline fun Stack.pushFrame(frame: ActivationFrame): Result<Unit, InvocationError> {
    return try {
        push(frame)
        Ok(Unit)
    } catch (_: IndexOutOfBoundsException) {
        Err(InvocationError.CallStackExhausted)
    } catch (_: IllegalArgumentException) {
        Err(InvocationError.CallStackExhausted)
    }
}

inline fun Stack.pushValue(value: ExecutionValue) = push(value)

inline fun Stack.peekFrame(): Result<ActivationFrame, InvocationError.MissingStackFrame> {
    return peekFrameOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackFrame)
}

inline fun Stack.peekLabel(): Result<Stack.Entry.Label, InvocationError.MissingStackLabel> {
    return peekLabelOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackLabel)
}

inline fun Stack.peekValue(): Result<ExecutionValue, InvocationError.MissingStackValue> {
    return peekValueOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.peekNthFrame(n: Int): Result<ActivationFrame, InvocationError.MissingStackFrame> {
    return peekNthFrameOrNull(n)?.let(::Ok) ?: Err(InvocationError.MissingStackFrame)
}

inline fun Stack.peekNthLabel(n: Int): Result<Stack.Entry.Label, InvocationError.MissingStackLabel> {
    return peekNthLabelOrNull(n)?.let(::Ok) ?: Err(InvocationError.MissingStackLabel)
}

inline fun Stack.peekNthValue(n: Int): Result<ExecutionValue, InvocationError.MissingStackValue> {
    return peekNthValueOrNull(n)?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popFrame(): Result<ActivationFrame, InvocationError.MissingStackFrame> {
    return popFrameOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackFrame)
}

inline fun Stack.popHandler(): Result<ExceptionHandler, InvocationError.UncaughtException> {
    return popHandlerOrNull()?.let(::Ok) ?: Err(InvocationError.UncaughtException)
}

inline fun Stack.popInstruction(): Result<DispatchableInstruction, InvocationError.MissingInstruction> {
    return popInstructionOrNull()?.let(::Ok) ?: Err(InvocationError.MissingInstruction)
}

inline fun Stack.popLabel(): Result<Stack.Entry.Label, InvocationError.MissingStackLabel> {
    return popLabelOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackLabel)
}

inline fun Stack.popValue(): Result<ExecutionValue, InvocationError.MissingStackValue> {
    return popValueOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popExecutionValue(): Result<ExecutionValue, InvocationError.MissingStackValue> {
    return popValueOrNull()?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popI32(): Result<Int, InvocationError.MissingStackValue> {
    return ((popValueOrNull() as? I32)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popI64(): Result<Long, InvocationError.MissingStackValue> {
    return ((popValueOrNull() as? I64)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popF32(): Result<Float, InvocationError.MissingStackValue> {
    return ((popValueOrNull() as? F32)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popF64(): Result<Double, InvocationError.MissingStackValue> {
    return ((popValueOrNull() as? F64)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popReference(): Result<ReferenceValue, InvocationError.MissingStackValue> {
    return (popValueOrNull() as? ReferenceValue)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.peekReference(): Result<ReferenceValue, InvocationError.MissingStackValue> {
    return (peekValueOrNull() as? ReferenceValue)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popNullReference(): Result<ReferenceValue.Null, InvocationError.MissingStackValue> {
    return (popValueOrNull() as? ReferenceValue.Null)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popI31Reference(): Result<ReferenceValue.I31, InvocationError.MissingStackValue> {
    return (popValueOrNull() as? ReferenceValue.I31)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popArrayReference(): Result<ReferenceValue.Array, InvocationError.MissingStackValue> {
    return (popValueOrNull() as? ReferenceValue.Array)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popStructReference(): Result<ReferenceValue.Struct, InvocationError.MissingStackValue> {
    return (popValueOrNull() as? ReferenceValue.Struct)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

inline fun Stack.popFunctionAddress(): Result<ReferenceValue.Function, InvocationError.FunctionReferenceExpected> {
    return (popValueOrNull() as? ReferenceValue.Function)?.let {
        Ok(it)
    } ?: Err(InvocationError.FunctionReferenceExpected)
}

inline fun Stack.constOperation(
    const: Int,
) = push(I32(const))

inline fun Stack.constOperation(
    const: Long,
) = push(I64(const))

inline fun Stack.constOperation(
    const: Float,
) = push(F32(const))

inline fun Stack.constOperation(
    const: Double,
) = push(F64(const))

inline fun <S, T : NumberValue<S>> Stack.unaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S) -> S,
): Result<Unit, InvocationError> {
    val operand = popValueOrNull() as T

    val result = operation(operand.value)
    push(constructor(result))
    return Ok(Unit)
}

@JvmName("i32UnaryOperation")
inline fun Stack.unaryOperation(
    crossinline operation: (Int) -> Int,
): Result<Unit, InvocationError> = unaryOperation(::I32, operation)

@JvmName("i64UnaryOperation")
inline fun Stack.unaryOperation(
    crossinline operation: (Long) -> Long,
): Result<Unit, InvocationError> = unaryOperation(::I64, operation)

@JvmName("f32UnaryOperation")
inline fun Stack.unaryOperation(
    crossinline operation: (Float) -> Float,
): Result<Unit, InvocationError> = unaryOperation(::F32, operation)

@JvmName("f64UnaryOperation")
inline fun Stack.unaryOperation(
    crossinline operation: (Double) -> Double,
): Result<Unit, InvocationError> = unaryOperation(::F64, operation)

inline fun <S, T : NumberValue<S>> Stack.binaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S, S) -> S,
): Result<Unit, InvocationError> {
    val operand2 = popValueOrNull() as T
    val operand1 = popValueOrNull() as T

    val result = operation(operand1.value, operand2.value)
    push(constructor(result))
    return Ok(Unit)
}

@JvmName("i32BinaryOperation")
inline fun Stack.binaryOperation(
    crossinline operation: (Int, Int) -> Int,
): Result<Unit, InvocationError> = binaryOperation(::I32, operation)

@JvmName("i64BinaryOperation")
inline fun Stack.binaryOperation(
    crossinline operation: (Long, Long) -> Long,
): Result<Unit, InvocationError> = binaryOperation(::I64, operation)

@JvmName("f32BinaryOperation")
inline fun Stack.binaryOperation(
    crossinline operation: (Float, Float) -> Float,
): Result<Unit, InvocationError> = binaryOperation(::F32, operation)

@JvmName("f64BinaryOperation")
inline fun Stack.binaryOperation(
    crossinline operation: (Double, Double) -> Double,
): Result<Unit, InvocationError> = binaryOperation(::F64, operation)

inline fun <S, T : NumberValue<S>> Stack.testOperation(
    crossinline operation: (S) -> Boolean,
): Result<Unit, InvocationError> {
    val operand = popValueOrNull() as T
    val result = if (operation(operand.value)) {
        I32(1)
    } else {
        I32(0)
    }
    push(result)
    return Ok(Unit)
}

inline fun <S, T : NumberValue<S>> Stack.relationalOperation(
    crossinline operation: (S, S) -> Boolean,
): Result<Unit, InvocationError> {
    val operand2 = popValueOrNull() as T
    val operand1 = popValueOrNull() as T
    val result = if (operation(operand1.value, operand2.value)) {
        I32(1)
    } else {
        I32(0)
    }
    push(result)
    return Ok(Unit)
}

inline fun <S, A : NumberValue<S>, T, B : NumberValue<T>> Stack.convertOperation(
    crossinline constructor: (T) -> B,
    crossinline operation: (S) -> T,
): Result<Unit, InvocationError> {
    val operand = popValueOrNull() as A
    val result = try {
        operation(operand.value)
    } catch (_: Throwable) {
        return Err(InvocationError.Trap.TrapEncountered)
    }
    push(constructor(result))
    return Ok(Unit)
}
