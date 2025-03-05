package io.github.charlietap.chasm.memory.write

import io.github.charlietap.chasm.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import liblinmem.write_bytes
import platform.posix.uint8_tVar

actual inline fun BytesWriter(
    memory: LinearMemory,
    memorySize: Int,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToWrite: Int,
    bufferPointer: Int,
) {
    val nativeMemory = memory as NativeLinearMemory

    buffer.usePinned { pinned ->
        val cValuesRef: CValuesRef<uint8_tVar>? = pinned.addressOf(bufferPointer).reinterpret()
        write_bytes(nativeMemory.pointer, memoryPointer, cValuesRef, bytesToWrite)
    }
}
