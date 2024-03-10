package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.ext.copyInto
import io.github.charlietap.chasm.executor.instantiator.ext.toDoubleLittleEndian
import io.github.charlietap.chasm.executor.instantiator.ext.toFloatLittleEndian
import io.github.charlietap.chasm.executor.instantiator.ext.toIntLittleEndian
import io.github.charlietap.chasm.executor.instantiator.ext.toLongLittleEndian
import io.github.charlietap.chasm.executor.instantiator.ext.toUIntLittleEndian
import io.github.charlietap.chasm.executor.instantiator.ext.toULongLittleEndian
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

internal data class ByteArrayLinearMemory(
    override val min: LinearMemory.Pages,
    override val max: LinearMemory.Pages,
    internal val memory: ByteArray,
) : LinearMemory {

    constructor(
        min: LinearMemory.Pages,
        max: LinearMemory.Pages = LinearMemory.Pages(LinearMemory.MAX_PAGES),
    ) : this(min, max, ByteArray(min.amount * LinearMemory.PAGE_SIZE))

    override fun grow(
        additionalPages: LinearMemory.Pages,
    ): Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits> {

        val proposedNumberOfPages = min.amount + additionalPages.amount

        if (proposedNumberOfPages > max.amount) {
            return Err(InvocationError.MemoryGrowExceedsLimits(additionalPages.amount, max.amount))
        }

        val newMemory = memory.copyOf(proposedNumberOfPages * LinearMemory.PAGE_SIZE)

        return Ok(ByteArrayLinearMemory(LinearMemory.Pages(proposedNumberOfPages), max, newMemory))
    }

    override fun readInt(offset: Int): Result<Int, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Int.SIZE_BYTES) {
            memory.toIntLittleEndian(offset)
        }

    override fun readInt(offset: Int, size: Int): Result<Int, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            memory.sliceArray(offset..(offset + size)).toIntLittleEndian()
        }

    override fun readLong(offset: Int): Result<Long, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Long.SIZE_BYTES) {
            memory.toLongLittleEndian(offset)
        }

    override fun readLong(offset: Int, size: Int): Result<Long, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            memory.sliceArray(offset..(offset + size)).toLongLittleEndian()
        }

    override fun readUInt(offset: Int): Result<UInt, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, UInt.SIZE_BYTES) {
            memory.toUIntLittleEndian(offset)
        }

    override fun readUInt(offset: Int, size: Int): Result<UInt, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            memory.sliceArray(offset..(offset + size)).toUIntLittleEndian()
        }

    override fun readULong(offset: Int): Result<ULong, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, ULong.SIZE_BYTES) {
            memory.toULongLittleEndian(offset)
        }

    override fun readULong(offset: Int, size: Int): Result<ULong, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            memory.sliceArray(offset..(offset + size)).toULongLittleEndian()
        }

    override fun readFloat(offset: Int): Result<Float, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Float.SIZE_BYTES) {
            memory.toFloatLittleEndian(offset)
        }

    override fun readDouble(offset: Int): Result<Double, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Double.SIZE_BYTES) {
            memory.toDoubleLittleEndian(offset)
        }

    override fun writeInt(value: Int, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Int.SIZE_BYTES) {
            value.copyInto(memory, offset)
        }

    override fun writeInt(value: Int, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            value.copyInto(memory, offset, size)
        }

    override fun writeLong(value: Long, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Long.SIZE_BYTES) {
            value.copyInto(memory, offset)
        }

    override fun writeLong(value: Long, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            value.copyInto(memory, offset, size)
        }

    override fun writeUInt(value: UInt, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, UInt.SIZE_BYTES) {
            value.copyInto(memory, offset)
        }

    override fun writeUInt(value: UInt, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            value.copyInto(memory, offset, size)
        }

    override fun writeULong(value: ULong, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, ULong.SIZE_BYTES) {
            value.copyInto(memory, offset)
        }

    override fun writeULong(value: ULong, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, size) {
            value.copyInto(memory, offset, size)
        }

    override fun writeFloat(value: Float, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> =
        runWithBoundsChecking(offset, Float.SIZE_BYTES) {
            value.copyInto(memory, offset)
        }

    override fun writeDouble(
        value: Double,
        offset: Int,
    ): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = runWithBoundsChecking(offset, Double.SIZE_BYTES) {
        value.copyInto(memory, offset)
    }

    private fun inBounds(offset: Int, size: Int) = (offset + size) <= memory.size

    private inline fun <T> runWithBoundsChecking(
        offset: Int,
        size: Int,
        crossinline operation: () -> T,
    ): Result<T, InvocationError.MemoryOperationOutOfBounds> = if (inBounds(offset, size)) {
        Ok(operation())
    } else {
        Err(InvocationError.MemoryOperationOutOfBounds)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ByteArrayLinearMemory

        if (min != other.min) return false
        if (max != other.max) return false
        if (!memory.contentEquals(other.memory)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        result = 31 * result + memory.contentHashCode()
        return result
    }
}
