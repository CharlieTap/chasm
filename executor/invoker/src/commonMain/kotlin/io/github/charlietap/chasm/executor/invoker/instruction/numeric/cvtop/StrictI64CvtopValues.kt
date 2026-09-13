package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64s
import io.github.charlietap.chasm.executor.invoker.ext.truncI64sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.executor.invoker.ext.truncI64uTrapping
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI64TruncF32S(operand: Float): Long = try {
    operand.truncI64sTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI64TruncF32U(operand: Float): Long = try {
    operand.truncI64uTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI64TruncF64S(operand: Double): Long = try {
    operand.truncI64sTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI64TruncF64U(operand: Double): Long = try {
    operand.truncI64uTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI64TruncSatF32S(operand: Float): Long = operand.truncI64s()

internal inline fun valueI64TruncSatF32U(operand: Float): Long = operand.truncI64u()

internal inline fun valueI64TruncSatF64S(operand: Double): Long = operand.truncI64s()

internal inline fun valueI64TruncSatF64U(operand: Double): Long = operand.truncI64u()
