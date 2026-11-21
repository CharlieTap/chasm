package io.github.charlietap.chasm.embedding.compiled

fun sizeMemory(memory: Memory): Int {
    return memory.engine.sizeMemory(memory)
}

fun memoryByteSize(memory: Memory): Int {
    return memory.engine.memoryByteSize(memory)
}

fun growMemory(
    memory: Memory,
    pagesToAdd: Int,
): Int {
    return memory.engine.growMemory(memory, pagesToAdd)
}

fun readByte(
    memory: Memory,
    pointer: Int,
): Byte {
    return memory.engine.readByte(memory, pointer)
}

fun readShort(
    memory: Memory,
    pointer: Int,
): Short {
    return memory.engine.readShort(memory, pointer)
}

fun readInt(
    memory: Memory,
    pointer: Int,
): Int {
    return memory.engine.readInt(memory, pointer)
}

fun readLong(
    memory: Memory,
    pointer: Int,
): Long {
    return memory.engine.readLong(memory, pointer)
}

fun readFloat(
    memory: Memory,
    pointer: Int,
): Float {
    return memory.engine.readFloat(memory, pointer)
}

fun readDouble(
    memory: Memory,
    pointer: Int,
): Double {
    return memory.engine.readDouble(memory, pointer)
}

fun readBytes(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int = 0,
): ByteArray {
    return memory.engine.readBytes(memory, buffer, memoryPointer, bytesToRead, bufferPointer)
}

fun readUtf8String(
    memory: Memory,
    pointer: Int,
    stringLengthInBytes: Int,
): String {
    return memory.engine.readUtf8String(memory, pointer, stringLengthInBytes)
}

fun readNullTerminatedUtf8String(
    memory: Memory,
    pointer: Int,
): String {
    return memory.engine.readNullTerminatedUtf8String(memory, pointer)
}

fun writeByte(
    memory: Memory,
    pointer: Int,
    value: Byte,
) {
    memory.engine.writeByte(memory, pointer, value)
}

fun writeShort(
    memory: Memory,
    pointer: Int,
    value: Short,
) {
    memory.engine.writeShort(memory, pointer, value)
}

fun writeInt(
    memory: Memory,
    pointer: Int,
    value: Int,
) {
    memory.engine.writeInt(memory, pointer, value)
}

fun writeLong(
    memory: Memory,
    pointer: Int,
    value: Long,
) {
    memory.engine.writeLong(memory, pointer, value)
}

fun writeFloat(
    memory: Memory,
    pointer: Int,
    value: Float,
) {
    memory.engine.writeFloat(memory, pointer, value)
}

fun writeDouble(
    memory: Memory,
    pointer: Int,
    value: Double,
) {
    memory.engine.writeDouble(memory, pointer, value)
}

fun writeBytes(
    memory: Memory,
    pointer: Int,
    buffer: ByteArray,
    bufferPointer: Int = 0,
    bytesToWrite: Int = buffer.size - bufferPointer,
) {
    memory.engine.writeBytes(memory, pointer, buffer, bufferPointer, bytesToWrite)
}

fun writeUtf8String(
    memory: Memory,
    pointer: Int,
    string: String,
) {
    memory.engine.writeUtf8String(memory, pointer, string)
}

fun fillMemory(
    memory: Memory,
    pointer: Int,
    value: Byte,
    bytesToFill: Int,
) {
    memory.engine.fillMemory(memory, pointer, value, bytesToFill)
}

fun copyMemory(
    source: Memory,
    sourcePointer: Int,
    destination: Memory,
    destinationPointer: Int,
    bytesToCopy: Int,
) {
    destination.engine.copyMemory(source, sourcePointer, destination, destinationPointer, bytesToCopy)
}

fun moveMemory(
    source: Memory,
    sourcePointer: Int,
    destination: Memory,
    destinationPointer: Int,
    bytesToMove: Int,
) {
    destination.engine.moveMemory(source, sourcePointer, destination, destinationPointer, bytesToMove)
}
