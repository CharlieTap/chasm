@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.ext

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue.I64
import kotlin.jvm.JvmName

internal inline fun Stack.peekFrameOrError(): Result<Stack.Entry.ActivationFrame, InvocationError.MissingStackFrame> {
    return peekFrame()?.let(::Ok) ?: Err(InvocationError.MissingStackFrame)
}

internal inline fun Stack.peekLabelOrError(): Result<Stack.Entry.Label, InvocationError.MissingStackLabel> {
    return peekLabel()?.let(::Ok) ?: Err(InvocationError.MissingStackLabel)
}

internal inline fun Stack.peekValueOrError(): Result<Stack.Entry.Value, InvocationError.MissingStackValue> {
    return peekValue()?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.popFrameOrError(): Result<Stack.Entry.ActivationFrame, InvocationError.MissingStackFrame> {
    return popFrame()?.let(::Ok) ?: Err(InvocationError.MissingStackFrame)
}

internal inline fun Stack.popLabelOrError(): Result<Stack.Entry.Label, InvocationError.MissingStackLabel> {
    return popLabel()?.let(::Ok) ?: Err(InvocationError.MissingStackLabel)
}

internal inline fun Stack.popValueOrError(): Result<Stack.Entry.Value, InvocationError.MissingStackValue> {
    return popValue()?.let(::Ok) ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.popI32(): Result<Int, InvocationError.MissingStackValue> {
    return ((popValue()?.value as? I32)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.popI64(): Result<Long, InvocationError.MissingStackValue> {
    return ((popValue()?.value as? I64)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.popF32(): Result<Float, InvocationError.MissingStackValue> {
    return ((popValue()?.value as? F32)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.popF64(): Result<Double, InvocationError.MissingStackValue> {
    return ((popValue()?.value as? F64)?.value)?.let {
        Ok(it)
    } ?: Err(InvocationError.MissingStackValue)
}

internal inline fun Stack.constOperation(
    const: Int,
) = push(Stack.Entry.Value(I32(const)))

internal inline fun Stack.constOperation(
    const: Long,
) = push(Stack.Entry.Value(I64(const)))

internal inline fun Stack.constOperation(
    const: Float,
) = push(Stack.Entry.Value(F32(const)))

internal inline fun Stack.constOperation(
    const: Double,
) = push(Stack.Entry.Value(F64(const)))

internal inline fun <S, T : NumberValue<S>> Stack.unaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S) -> S,
): Result<Unit, InvocationError> {
    val operand = popValue()?.value as T

    val result = operation(operand.value)
    push(Stack.Entry.Value(constructor(result)))
    return Ok(Unit)
}

@JvmName("i32UnaryOperation")
internal inline fun Stack.unaryOperation(
    crossinline operation: (Int) -> Int,
): Result<Unit, InvocationError> = unaryOperation(::I32, operation)

@JvmName("i64UnaryOperation")
internal inline fun Stack.unaryOperation(
    crossinline operation: (Long) -> Long,
): Result<Unit, InvocationError> = unaryOperation(::I64, operation)

@JvmName("f32UnaryOperation")
internal inline fun Stack.unaryOperation(
    crossinline operation: (Float) -> Float,
): Result<Unit, InvocationError> = unaryOperation(::F32, operation)

@JvmName("f64UnaryOperation")
internal inline fun Stack.unaryOperation(
    crossinline operation: (Double) -> Double,
): Result<Unit, InvocationError> = unaryOperation(::F64, operation)

internal inline fun <S, T : NumberValue<S>> Stack.binaryOperation(
    crossinline constructor: (S) -> T,
    crossinline operation: (S, S) -> S,
): Result<Unit, InvocationError> {
    val operand2 = popValue()?.value as T
    val operand1 = popValue()?.value as T

    val result = operation(operand1.value, operand2.value)
    push(Stack.Entry.Value(constructor(result)))
    return Ok(Unit)
}

@JvmName("i32BinaryOperation")
internal inline fun Stack.binaryOperation(
    crossinline operation: (Int, Int) -> Int,
): Result<Unit, InvocationError> = binaryOperation(::I32, operation)

@JvmName("i64BinaryOperation")
internal inline fun Stack.binaryOperation(
    crossinline operation: (Long, Long) -> Long,
): Result<Unit, InvocationError> = binaryOperation(::I64, operation)

@JvmName("f32BinaryOperation")
internal inline fun Stack.binaryOperation(
    crossinline operation: (Float, Float) -> Float,
): Result<Unit, InvocationError> = binaryOperation(::F32, operation)

@JvmName("f64BinaryOperation")
internal inline fun Stack.binaryOperation(
    crossinline operation: (Double, Double) -> Double,
): Result<Unit, InvocationError> = binaryOperation(::F64, operation)

internal inline fun <S, T : NumberValue<S>> Stack.testOperation(
    pass: T,
    fail: T,
    crossinline operation: (S) -> Boolean,
): Result<Unit, InvocationError> {
    val operand = popValue()?.value as T
    val result = if (operation(operand.value)) {
        pass
    } else {
        fail
    }
    push(Stack.Entry.Value(result))
    return Ok(Unit)
}

@JvmName("i32TestOperation")
internal inline fun Stack.testOperation(
    crossinline operation: (Int) -> Boolean,
): Result<Unit, InvocationError> =
    testOperation(I32(1), I32(0), operation)

@JvmName("i64TestOperation")
internal inline fun Stack.testOperation(
    crossinline operation: (Long) -> Boolean,
): Result<Unit, InvocationError> =
    testOperation(I64(1), I64(0), operation)

internal inline fun <S, T : NumberValue<S>> Stack.relationalOperation(
    pass: T,
    fail: T,
    crossinline operation: (S, S) -> Boolean,
): Result<Unit, InvocationError> {
    val operand2 = popValue()?.value as T
    val operand1 = popValue()?.value as T
    val result = if (operation(operand1.value, operand2.value)) {
        pass
    } else {
        fail
    }
    push(Stack.Entry.Value(result))
    return Ok(Unit)
}

@JvmName("i32RelationalOperation")
internal inline fun Stack.relationalOperation(
    crossinline operation: (Int, Int) -> Boolean,
): Result<Unit, InvocationError> =
    relationalOperation(I32(1), I32(0), operation)

@JvmName("i64RelationalOperation")
internal inline fun Stack.relationalOperation(
    crossinline operation: (Long, Long) -> Boolean,
): Result<Unit, InvocationError> =
    relationalOperation(I64(1), I64(0), operation)

@JvmName("f32RelationalOperation")
internal inline fun Stack.relationalOperation(
    crossinline operation: (Float, Float) -> Boolean,
): Result<Unit, InvocationError> =
    relationalOperation(F32(1f), F32(0f), operation)

@JvmName("f64RelationalOperation")
internal inline fun Stack.relationalOperation(
    crossinline operation: (Double, Double) -> Boolean,
): Result<Unit, InvocationError> =
    relationalOperation(F64(1.0), F64(0.0), operation)

internal inline fun <S, A : NumberValue<S>, T, B : NumberValue<T>> Stack.convertOperation(
    crossinline constructor: (T) -> B,
    crossinline operation: (S) -> T,
): Result<Unit, InvocationError> {
    val operand = popValue()?.value as A
    val result = operation(operand.value)
    push(Stack.Entry.Value(constructor(result)))
    return Ok(Unit)
}
