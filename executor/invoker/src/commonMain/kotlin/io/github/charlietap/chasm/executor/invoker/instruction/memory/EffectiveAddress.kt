package io.github.charlietap.chasm.executor.invoker.instruction.memory

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException

internal inline fun effectiveAddress(
    address: Int,
    offset: Int,
): Int {
    // Temporary while linear memory is capped below 2 GiB. Wasm adds address
    // and offset as unsigned values without wrapping, but this is Int maths,
    // so a negative operand could wrap the sum back into bounds. Any negative
    // operand is out of bounds anyway, so trap before adding. Move to Long
    // arithmetic once larger memories are supported.
    if (address < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
    return address + offset
}
