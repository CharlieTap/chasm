@file:JvmName("LinearMemoryFillerJvm")

package io.github.charlietap.chasm.memory.fill

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.util.Arrays

private const val BULK_FILL_THRESHOLD = 64
private val zeroFillChunk = ByteArray(LinearMemory.PAGE_SIZE)
private val nonZeroFillChunk = ThreadLocal.withInitial { ByteArray(LinearMemory.PAGE_SIZE) }

actual fun LinearMemoryFiller(
    memory: LinearMemory,
    address: Int,
    bytesToFill: Int,
    fillValue: Byte,
    upperBound: Int,
) {
    if ((bytesToFill or address) < 0 || bytesToFill > upperBound - address) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val buffer = (memory as ByteBufferLinearMemory).memory

    val slice = buffer.duplicate().apply {
        position(address)
        limit(address + bytesToFill)
    }

    if (bytesToFill < BULK_FILL_THRESHOLD) {
        repeat(bytesToFill) {
            slice.put(fillValue)
        }
        return
    }

    val chunkSize = minOf(bytesToFill, LinearMemory.PAGE_SIZE)
    val fillChunk: ByteArray
    if (fillValue.toInt() == 0) {
        fillChunk = zeroFillChunk
    } else {
        fillChunk = nonZeroFillChunk.get()
        Arrays.fill(fillChunk, 0, chunkSize, fillValue)
    }
    while (slice.hasRemaining()) {
        slice.put(fillChunk, 0, minOf(slice.remaining(), chunkSize))
    }
}
