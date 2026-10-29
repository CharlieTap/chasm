@file:JvmName("LinearMemoryLoaderAndroid")

package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
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
    val destination = dst as ByteArrayLinearMemory
    if (
        (dstOffset or bytesToLoad) < 0 ||
        bytesToLoad > dstUpperBound - dstOffset ||
        bytesToLoad > destination.byteSize - dstOffset
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
    load(destination.bytes, dstOffset)
}
