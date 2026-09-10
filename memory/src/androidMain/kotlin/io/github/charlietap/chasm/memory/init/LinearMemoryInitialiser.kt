@file:JvmName("LinearMemoryInitialiserAndroid")

package io.github.charlietap.chasm.memory.init

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory

actual inline fun LinearMemoryInitialiser(
    src: UByteArray,
    dst: LinearMemory,
    srcOffset: Int,
    dstOffset: Int,
    bytesToInit: Int,
    srcUpperBound: Int,
    dstUpperBound: Int,
) {
    if (
        (srcOffset or dstOffset or bytesToInit) < 0 ||
        bytesToInit > srcUpperBound - srcOffset ||
        bytesToInit > dstUpperBound - dstOffset
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
    try {
        val destination = dst as ByteArrayLinearMemory
        BoundsChecker(dstOffset, bytesToInit, destination.byteSize)
        BoundsChecker(srcOffset, bytesToInit, src.size)
        src.asByteArray().copyInto(destination.bytes, dstOffset, srcOffset, srcOffset + bytesToInit)
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}
