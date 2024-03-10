package io.github.charlietap.chasm.executor.runtime.memory

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import kotlin.jvm.JvmInline

interface LinearMemory {

    @JvmInline
    value class Pages(val amount: Int)

    val min: Pages
    val max: Pages?

    fun grow(additionalPages: Pages): Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits>

    fun readInt(offset: Int): Result<Int, InvocationError.MemoryOperationOutOfBounds>

    fun readInt(offset: Int, size: Int): Result<Int, InvocationError.MemoryOperationOutOfBounds>

    fun readLong(offset: Int): Result<Long, InvocationError.MemoryOperationOutOfBounds>

    fun readLong(offset: Int, size: Int): Result<Long, InvocationError.MemoryOperationOutOfBounds>

    fun readUInt(offset: Int): Result<UInt, InvocationError.MemoryOperationOutOfBounds>

    fun readUInt(offset: Int, size: Int): Result<UInt, InvocationError.MemoryOperationOutOfBounds>

    fun readULong(offset: Int): Result<ULong, InvocationError.MemoryOperationOutOfBounds>

    fun readULong(offset: Int, size: Int): Result<ULong, InvocationError.MemoryOperationOutOfBounds>

    fun readFloat(offset: Int): Result<Float, InvocationError.MemoryOperationOutOfBounds>

    fun readDouble(offset: Int): Result<Double, InvocationError.MemoryOperationOutOfBounds>

    fun writeInt(value: Int, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeInt(value: Int, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeLong(value: Long, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeLong(value: Long, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeUInt(value: UInt, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeUInt(value: UInt, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeULong(value: ULong, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeULong(value: ULong, offset: Int, size: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeFloat(value: Float, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun writeDouble(value: Double, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds>

    fun size(): Long = min.amount.toLong() * PAGE_SIZE

    companion object {
        const val PAGE_SIZE = 65536
        const val MAX_PAGES = PAGE_SIZE
    }
}
