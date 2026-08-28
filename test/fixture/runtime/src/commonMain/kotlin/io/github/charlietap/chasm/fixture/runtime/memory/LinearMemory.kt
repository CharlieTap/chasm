package io.github.charlietap.chasm.fixture.runtime.memory

import io.github.charlietap.chasm.host.HostMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory

object NoOpLinearMemory : LinearMemory {
    override val byteSize: Int = 0

    override fun readI8(memoryPointer: Int): Byte = error("NoOpLinearMemory")

    override fun readI16(memoryPointer: Int): Short = error("NoOpLinearMemory")

    override fun readI32(memoryPointer: Int): Int = error("NoOpLinearMemory")

    override fun readI64(memoryPointer: Int): Long = error("NoOpLinearMemory")

    override fun readF32(memoryPointer: Int): Float = error("NoOpLinearMemory")

    override fun readF64(memoryPointer: Int): Double = error("NoOpLinearMemory")

    override fun read(buffer: ByteArray, memoryPointer: Int, bytesToRead: Int, bufferPointer: Int): ByteArray =
        error("NoOpLinearMemory")

    override fun writeI8(memoryPointer: Int, value: Byte) = error("NoOpLinearMemory")

    override fun writeI16(memoryPointer: Int, value: Short) = error("NoOpLinearMemory")

    override fun writeI32(memoryPointer: Int, value: Int) = error("NoOpLinearMemory")

    override fun writeI64(memoryPointer: Int, value: Long) = error("NoOpLinearMemory")

    override fun writeF32(memoryPointer: Int, value: Float) = error("NoOpLinearMemory")

    override fun writeF64(memoryPointer: Int, value: Double) = error("NoOpLinearMemory")

    override fun write(memoryPointer: Int, buffer: ByteArray, bufferPointer: Int, bytesToWrite: Int) =
        error("NoOpLinearMemory")

    override fun fill(memoryPointer: Int, value: Byte, bytesToFill: Int) = error("NoOpLinearMemory")

    override fun copy(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToCopy: Int,
        source: HostMemory,
    ) = error("NoOpLinearMemory")

    override fun move(
        sourcePointer: Int,
        destinationPointer: Int,
        bytesToMove: Int,
        source: HostMemory,
    ) = error("NoOpLinearMemory")
}

fun linearMemory() = NoOpLinearMemory
