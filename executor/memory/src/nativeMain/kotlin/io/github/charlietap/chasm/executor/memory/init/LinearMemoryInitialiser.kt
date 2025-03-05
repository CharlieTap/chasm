package io.github.charlietap.chasm.executor.memory.init

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import liblinmem.write_bytes
import platform.posix.uint8_tVar

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
        bytesToInit < 0 ||
        srcOffset < 0 ||
        dstOffset < 0 ||
        (srcOffset + bytesToInit) > srcUpperBound ||
        (dstOffset + bytesToInit) > dstUpperBound
    ) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    if (bytesToInit == 0) return

    val nativeMemory = dst as NativeLinearMemory
    src.usePinned { pinned ->
        val cValuesRef: CValuesRef<uint8_tVar>? = pinned.addressOf(srcOffset).reinterpret()
        write_bytes(nativeMemory.pointer, dstOffset, cValuesRef, bytesToInit)
    }
}
