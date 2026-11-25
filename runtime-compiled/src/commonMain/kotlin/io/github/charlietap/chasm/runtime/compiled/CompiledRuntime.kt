@file:JvmName("CompiledRuntime")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import kotlin.jvm.JvmName

@InternalChasmApi
fun fuelAndInterruptCheck(
    context: ExecutionContext,
) {
    val store = context.store
    if (store.interrupt.requested) throw InvocationException(InvocationError.Interrupted)
    val fuel = store.fuel
    if (fuel.remaining <= 0L) throw InvocationException(InvocationError.FuelExhausted)
    fuel.remaining--
}

@InternalChasmApi
fun fuelCheck(
    context: ExecutionContext,
) {
    val fuel = context.store.fuel
    if (fuel.remaining <= 0L) throw InvocationException(InvocationError.FuelExhausted)
    fuel.remaining--
}

@InternalChasmApi
fun interruptCheck(
    context: ExecutionContext,
) {
    if (context.store.interrupt.requested) throw InvocationException(InvocationError.Interrupted)
}

@InternalChasmApi
fun unreachable(): Nothing = throw InvocationException(InvocationError.Unreachable)

@InternalChasmApi
fun callStackExhausted(): Nothing = throw InvocationException(InvocationError.CallStackExhausted)

@InternalChasmApi
fun divideByZero(): Nothing = throw InvocationException(InvocationError.CannotDivideIntegerByZero)

@InternalChasmApi
fun nullReference(): Nothing {
    throw InvocationException(InvocationError.NonNullReferenceExpected)
}

@InternalChasmApi
fun integerOverflow(): Nothing = throw InvocationException(InvocationError.IntegerOverflow)

@InternalChasmApi
fun conversionFailed(): Nothing = throw InvocationException(InvocationError.ConvertOperationFailed)

@InternalChasmApi
fun memoryOutOfBounds(): Nothing = throw InvocationException(InvocationError.MemoryOperationOutOfBounds)

@InternalChasmApi
fun unpackLongs(
    parts: Array<String>,
): LongArray {
    var count = 0
    for (part in parts) for (char in part) if (char.code and CONTINUES == 0) count++
    val values = LongArray(count)
    var index = 0
    var value = 0L
    var shift = 0
    for (part in parts) {
        for (char in part) {
            val group = char.code
            value = value or ((group and GROUP).toLong() shl shift)
            shift += GROUP_BITS
            if (group and CONTINUES != 0) continue
            if (shift < Long.SIZE_BITS && group and SIGN != 0) value = value or (-1L shl shift)
            values[index++] = value
            value = 0L
            shift = 0
        }
    }
    return values
}

private const val GROUP_BITS = 7
private const val GROUP = 0x7F
private const val SIGN = 0x40
private const val CONTINUES = 0x80
