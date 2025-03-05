package io.github.charlietap.chasm.executor.memory.write

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import liblinmem.write_bytes
import platform.posix.uint8_tVar

actual inline fun StringWriter(
    memory: LinearMemory,
    memoryPointer: Int,
    string: String,
) {
    val nativeMemory = memory as NativeLinearMemory

    val bytes = string.encodeToByteArray()
    bytes.usePinned { pinned ->
        val cValuesRef: CPointer<uint8_tVar> = pinned.addressOf(0).reinterpret()
        write_bytes(nativeMemory.pointer, memoryPointer, cValuesRef, bytes.size)
    }
}
