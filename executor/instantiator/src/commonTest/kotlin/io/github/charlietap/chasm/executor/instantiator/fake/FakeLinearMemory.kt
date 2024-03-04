package io.github.charlietap.chasm.executor.instantiator.fake

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import kotlin.test.fail

class FakeLinearMemory(
    override val min: LinearMemory.Pages = LinearMemory.Pages(0),
    override val max: LinearMemory.Pages = LinearMemory.Pages(0),
    private val fakeGrow: (
        LinearMemory.Pages,
    ) -> Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits> = { fail("grow should not be called in this scenario") },
    private val fakeReadInt: (
        Int,
    ) -> Result<Int, InvocationError.MemoryOperationOutOfBounds> = { fail("readInt should not be called in this scenario") },
    private val fakeReadLong: (
        Int,
    ) -> Result<Long, InvocationError.MemoryOperationOutOfBounds> = { fail("readLong should not be called in this scenario") },
    private val fakeReadUInt: (
        Int,
    ) -> Result<UInt, InvocationError.MemoryOperationOutOfBounds> = { fail("readUInt should not be called in this scenario") },
    private val fakeReadULong: (
        Int,
    ) -> Result<ULong, InvocationError.MemoryOperationOutOfBounds> = { fail("readULong should not be called in this scenario") },
    private val fakeReadFloat: (
        Int,
    ) -> Result<Float, InvocationError.MemoryOperationOutOfBounds> = { fail("readFloat should not be called in this scenario") },
    private val fakeReadDouble: (
        Int,
    ) -> Result<Double, InvocationError.MemoryOperationOutOfBounds> = { fail("readDouble should not be called in this scenario") },
    private val fakeWriteInt: (
        Int,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeInt should not be called in this scenario") },
    private val fakeWriteLong: (
        Long,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeLong should not be called in this scenario") },
    private val fakeWriteUInt: (
        UInt,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeUInt should not be called in this scenario") },
    private val fakeWriteULong: (
        ULong,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeULong should not be called in this scenario") },
    private val fakeWriteFloat: (
        Float,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeFloat should not be called in this scenario") },
    private val fakeWriteDouble: (
        Double,
        Int,
    ) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds> = { _, _ -> fail("writeDouble should not be called in this scenario") },
) : LinearMemory {

    override fun grow(
        additionalPages: LinearMemory.Pages,
    ): Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits> = fakeGrow(additionalPages)

    override fun readInt(offset: Int): Result<Int, InvocationError.MemoryOperationOutOfBounds> = fakeReadInt(offset)

    override fun readLong(offset: Int): Result<Long, InvocationError.MemoryOperationOutOfBounds> = fakeReadLong(offset)

    override fun readUInt(offset: Int): Result<UInt, InvocationError.MemoryOperationOutOfBounds> = fakeReadUInt(offset)

    override fun readULong(offset: Int): Result<ULong, InvocationError.MemoryOperationOutOfBounds> = fakeReadULong(offset)

    override fun readFloat(offset: Int): Result<Float, InvocationError.MemoryOperationOutOfBounds> = fakeReadFloat(offset)

    override fun readDouble(offset: Int): Result<Double, InvocationError.MemoryOperationOutOfBounds> = fakeReadDouble(offset)

    override fun writeInt(value: Int, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteInt(value, offset)

    override fun writeLong(value: Long, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteLong(
        value,
        offset,
    )

    override fun writeUInt(value: UInt, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteUInt(
        value,
        offset,
    )

    override fun writeULong(value: ULong, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteULong(
        value,
        offset,
    )

    override fun writeFloat(value: Float, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteFloat(
        value,
        offset,
    )

    override fun writeDouble(value: Double, offset: Int): Result<Unit, InvocationError.MemoryOperationOutOfBounds> = fakeWriteDouble(
        value,
        offset,
    )
}

fun FakeLinearMemoryGrow(
    min: LinearMemory.Pages = LinearMemory.Pages(0),
    max: LinearMemory.Pages = LinearMemory.Pages(0),
    fakeGrow: (LinearMemory.Pages) -> Result<LinearMemory, InvocationError.MemoryGrowExceedsLimits>,
) = FakeLinearMemory(
    min = min,
    max = max,
    fakeGrow = fakeGrow,
)
