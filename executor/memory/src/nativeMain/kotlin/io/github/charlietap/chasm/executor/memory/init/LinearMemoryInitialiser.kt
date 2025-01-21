package io.github.charlietap.chasm.executor.memory.init

import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
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
) {
    val nativeMemory = dst as NativeLinearMemory
    src.usePinned { pinned ->
        val cValuesRef: CValuesRef<uint8_tVar>? = pinned.addressOf(srcOffset).reinterpret()
        write_bytes(nativeMemory.pointer, dstOffset, cValuesRef, bytesToInit)
    }
}
