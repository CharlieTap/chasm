package io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32s
import io.github.charlietap.chasm.executor.invoker.ext.truncI32sTrapping
import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.executor.invoker.ext.truncI32uTrapping
import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

// Shared scalar semantics for frame executors and generated Kotlin values.
internal inline fun valueI32TruncF32S(operand: Float): Int = try {
    operand.truncI32sTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI32TruncF32U(operand: Float): Int = try {
    operand.truncI32uTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI32TruncF64S(operand: Double): Int = try {
    operand.truncI32sTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI32TruncF64U(operand: Double): Int = try {
    operand.truncI32uTrapping()
} catch (_: IllegalArgumentException) {
    throw InvocationException(InvocationError.ConvertOperationFailed)
}

internal inline fun valueI32TruncSatF32S(operand: Float): Int = operand.truncI32s()

internal inline fun valueI32TruncSatF32U(operand: Float): Int = operand.truncI32u()

internal inline fun valueI32TruncSatF64S(operand: Double): Int = operand.truncI32s()

internal inline fun valueI32TruncSatF64U(operand: Double): Int = operand.truncI32u()

internal inline fun valueI32WrapI64(operand: Long): Int = operand.wrap()
