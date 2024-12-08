package io.github.charlietap.chasm.executor.memory.init

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.NativeLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
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
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = binding {
    val nativeMemory = dst as NativeLinearMemory
    src.usePinned { pinned ->
        val cValuesRef: CValuesRef<uint8_tVar>? = pinned.addressOf(srcOffset).reinterpret()
        write_bytes(nativeMemory.pointer, dstOffset, cValuesRef, bytesToInit)
    }
}
