@file:JvmName("LinearMemoryLoaderJvm")

package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryLoader(
    dst: LinearMemory,
    dstOffset: Int,
    bytesToLoad: Int,
    dstUpperBound: Int,
    load: (ByteArray, Int) -> Unit,
) {
    val buffer = (dst as ByteBufferLinearMemory).memory
    if (
        (dstOffset or bytesToLoad) < 0 ||
        bytesToLoad > dstUpperBound - dstOffset ||
        bytesToLoad > buffer.capacity() - dstOffset
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
    val bytes = ByteArray(bytesToLoad)
    load(bytes, 0)
    buffer.put(dstOffset, bytes, 0, bytesToLoad)
}
