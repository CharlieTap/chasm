package io.github.charlietap.chasm.embedding.compiled.host

import io.github.charlietap.chasm.embedding.compiled.Memory
import io.github.charlietap.chasm.embedding.compiled.copyMemory
import io.github.charlietap.chasm.embedding.compiled.fillMemory
import io.github.charlietap.chasm.embedding.compiled.growMemory
import io.github.charlietap.chasm.embedding.compiled.memoryByteSize
import io.github.charlietap.chasm.embedding.compiled.moveMemory
import io.github.charlietap.chasm.embedding.compiled.readByte
import io.github.charlietap.chasm.embedding.compiled.readBytes
import io.github.charlietap.chasm.embedding.compiled.readDouble
import io.github.charlietap.chasm.embedding.compiled.readFloat
import io.github.charlietap.chasm.embedding.compiled.readInt
import io.github.charlietap.chasm.embedding.compiled.readLong
import io.github.charlietap.chasm.embedding.compiled.readShort
import io.github.charlietap.chasm.embedding.compiled.writeByte
import io.github.charlietap.chasm.embedding.compiled.writeBytes
import io.github.charlietap.chasm.embedding.compiled.writeDouble
import io.github.charlietap.chasm.embedding.compiled.writeFloat
import io.github.charlietap.chasm.embedding.compiled.writeInt
import io.github.charlietap.chasm.embedding.compiled.writeLong
import io.github.charlietap.chasm.embedding.compiled.writeShort
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.embedding.compiled.readNullTerminatedUtf8String as catalogueReadNullTerminatedUtf8String
import io.github.charlietap.chasm.embedding.compiled.readUtf8String as catalogueReadUtf8String
import io.github.charlietap.chasm.embedding.compiled.writeUtf8String as catalogueWriteUtf8String

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withMemory(
    index: ModuleIndex.MemoryIndex,
    block: context(ModuleIndex.MemoryIndex) Memory.() -> T,
): T {
    return context(index) { resources.memory(module, index).block() }
}

context(module: HostModuleInstance, resources: HostResources)
inline fun <T> withMemory(
    index: Int,
    block: context(ModuleIndex.MemoryIndex) Memory.() -> T,
): T {
    return withMemory(ModuleIndex.MemoryIndex(index), block)
}

inline val Memory.byteSize: Int
    get() = memoryByteSize(this)

inline fun Memory.readI8(memoryPointer: Int): Byte {
    return readByte(this, memoryPointer)
}

inline fun Memory.readI16(memoryPointer: Int): Short {
    return readShort(this, memoryPointer)
}

inline fun Memory.readI32(memoryPointer: Int): Int {
    return readInt(this, memoryPointer)
}

inline fun Memory.readI64(memoryPointer: Int): Long {
    return readLong(this, memoryPointer)
}

inline fun Memory.readF32(memoryPointer: Int): Float {
    return readFloat(this, memoryPointer)
}

inline fun Memory.readF64(memoryPointer: Int): Double {
    return readDouble(this, memoryPointer)
}

inline fun Memory.readU8(memoryPointer: Int): UByte {
    return readI8(memoryPointer).toUByte()
}

inline fun Memory.readU16(memoryPointer: Int): UShort {
    return readI16(memoryPointer).toUShort()
}

inline fun Memory.readU32(memoryPointer: Int): UInt {
    return readI32(memoryPointer).toUInt()
}

inline fun Memory.readU64(memoryPointer: Int): ULong {
    return readI64(memoryPointer).toULong()
}

inline fun Memory.writeI8(
    memoryPointer: Int,
    value: Byte,
) {
    writeByte(this, memoryPointer, value)
}

inline fun Memory.writeI16(
    memoryPointer: Int,
    value: Short,
) {
    writeShort(this, memoryPointer, value)
}

inline fun Memory.writeI32(
    memoryPointer: Int,
    value: Int,
) {
    writeInt(this, memoryPointer, value)
}

inline fun Memory.writeI64(
    memoryPointer: Int,
    value: Long,
) {
    writeLong(this, memoryPointer, value)
}

inline fun Memory.writeF32(
    memoryPointer: Int,
    value: Float,
) {
    writeFloat(this, memoryPointer, value)
}

inline fun Memory.writeF64(
    memoryPointer: Int,
    value: Double,
) {
    writeDouble(this, memoryPointer, value)
}

inline fun Memory.read(
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int = 0,
): ByteArray {
    return readBytes(this, buffer, memoryPointer, bytesToRead, bufferPointer)
}

inline fun Memory.write(
    memoryPointer: Int,
    buffer: ByteArray,
    bufferPointer: Int = 0,
    bytesToWrite: Int = buffer.size - bufferPointer,
) {
    writeBytes(this, memoryPointer, buffer, bufferPointer, bytesToWrite)
}

inline fun Memory.fill(
    memoryPointer: Int,
    value: Byte,
    bytesToFill: Int,
) {
    fillMemory(this, memoryPointer, value, bytesToFill)
}

inline fun Memory.copy(
    sourcePointer: Int,
    destinationPointer: Int,
    bytesToCopy: Int,
    source: Memory = this,
) {
    copyMemory(source, sourcePointer, this, destinationPointer, bytesToCopy)
}

inline fun Memory.move(
    sourcePointer: Int,
    destinationPointer: Int,
    bytesToMove: Int,
    source: Memory = this,
) {
    moveMemory(source, sourcePointer, this, destinationPointer, bytesToMove)
}

inline fun Memory.grow(pagesToAdd: Int): Int {
    return growMemory(this, pagesToAdd)
}

inline fun Memory.readUtf8String(
    memoryPointer: Int,
    stringLengthInBytes: Int,
): String {
    return catalogueReadUtf8String(this, memoryPointer, stringLengthInBytes)
}

inline fun Memory.readNullTerminatedUtf8String(memoryPointer: Int): String {
    return catalogueReadNullTerminatedUtf8String(this, memoryPointer)
}

inline fun Memory.writeUtf8String(
    memoryPointer: Int,
    string: String,
) {
    catalogueWriteUtf8String(this, memoryPointer, string)
}
