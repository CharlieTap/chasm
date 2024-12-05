package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceCopier = (MemoryInstance, MemoryInstance, IntRange, IntRange) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceCopier(
    src: MemoryInstance,
    dst: MemoryInstance,
    srcRange: IntRange,
    dstRange: IntRange,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val srcByteArray = (src.data as ByteArrayLinearMemory).memory
    val dstByteArray = (dst.data as ByteArrayLinearMemory).memory

    return MemoryInstanceCopier(
        src = srcByteArray,
        dst = dstByteArray,
        srcRange = srcRange,
        dstRange = dstRange,
    )
}

internal inline fun MemoryInstanceCopier(
    src: ByteArray,
    dst: ByteArray,
    srcRange: IntRange,
    dstRange: IntRange,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = binding {

    if (!src.indices.contains(srcRange) || !dst.indices.contains(dstRange)) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    try {
        src.copyInto(dst, dstRange.first, srcRange.first, srcRange.last + 1)
    } catch (_: IndexOutOfBoundsException) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    } catch (_: IllegalArgumentException) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }
}
