@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.memory.copy

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

fun MemoryInstanceCopierImpl(
    src: MemoryInstance,
    dst: MemoryInstance,
    srcRange: IntRange,
    dstRange: IntRange,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val srcByteArray = (src.data as ByteArrayLinearMemory).memory
    val dstByteArray = (dst.data as ByteArrayLinearMemory).memory

    return MemoryInstanceCopierImpl(
        src = srcByteArray,
        dst = dstByteArray,
        srcRange = srcRange,
        dstRange = dstRange,
    )
}

internal inline fun MemoryInstanceCopierImpl(
    src: ByteArray,
    dst: ByteArray,
    srcRange: IntRange,
    dstRange: IntRange,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = binding {

    if (!src.indices.contains(srcRange) || !dst.indices.contains(dstRange)) {
        Err(InvocationError.MemoryOperationOutOfBounds).bind<Unit>()
    }

    src.copyInto(dst, dstRange.first, srcRange.first, srcRange.last + 1)
}
