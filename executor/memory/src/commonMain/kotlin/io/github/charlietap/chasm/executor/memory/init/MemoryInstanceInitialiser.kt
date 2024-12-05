package io.github.charlietap.chasm.executor.memory.init

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.contains
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance

typealias MemoryInstanceInitialiser = (DataInstance, MemoryInstance, IntRange, IntRange) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>

fun MemoryInstanceInitialiser(
    src: DataInstance,
    dst: MemoryInstance,
    srcRange: IntRange,
    dstRange: IntRange,
): Result<Unit, InvocationError.MemoryOperationOutOfBounds> {

    val srcByteArray = src.bytes.asByteArray()
    val dstByteArray = (dst.data as ByteArrayLinearMemory).memory

    return MemoryInstanceInitialiser(
        src = srcByteArray,
        dst = dstByteArray,
        srcRange = srcRange,
        dstRange = dstRange,
    )
}

internal inline fun MemoryInstanceInitialiser(
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
