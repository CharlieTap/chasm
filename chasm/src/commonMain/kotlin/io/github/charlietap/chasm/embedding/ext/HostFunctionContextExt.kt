package io.github.charlietap.chasm.embedding.ext

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.memory.readByte
import io.github.charlietap.chasm.embedding.memory.readBytes
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.map

inline fun HostFunctionContext.byte(
    memory: Memory,
    memoryPointer: Int,
): ChasmResult<Byte, ChasmError.ExecutionError> = readByte(this.store, memory, memoryPointer)

inline fun HostFunctionContext.bytes(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int = 0,
): ChasmResult<ByteArray, ChasmError.ExecutionError> = readBytes(this.store, memory, buffer, memoryPointer, bytesToRead, bufferPointer)

inline fun HostFunctionContext.int(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bufferPointer: Int = 0,
): ChasmResult<Int, ChasmError.ExecutionError> = readBytes(this.store, memory, buffer, memoryPointer, Int.SIZE_BYTES, bufferPointer).map {
        bytes ->
    var result: Int = 0
    for (i in 0 until Int.SIZE_BYTES) {
        result = result or (bytes[i].toInt() shl Byte.SIZE_BITS * i)
    }
    result
}

inline fun HostFunctionContext.uint(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bufferPointer: Int = 0,
): ChasmResult<UInt, ChasmError.ExecutionError> = readBytes(this.store, memory, buffer, memoryPointer, UInt.SIZE_BYTES, bufferPointer).map {
        bytes ->
    var result: UInt = 0u
    for (i in 0 until Int.SIZE_BYTES) {
        result = result or (bytes[i].toUInt() shl (Byte.SIZE_BITS * i))
    }
    result
}

inline fun HostFunctionContext.long(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bufferPointer: Int = 0,
): ChasmResult<Long, ChasmError.ExecutionError> = readBytes(this.store, memory, buffer, memoryPointer, Long.SIZE_BYTES, bufferPointer).map {
        bytes ->
    var result: Long = 0
    for (i in 0 until Long.SIZE_BYTES) {
        result = result or (bytes[i].toLong() shl Byte.SIZE_BITS * i)
    }
    result
}

inline fun HostFunctionContext.ulong(
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bufferPointer: Int = 0,
): ChasmResult<ULong, ChasmError.ExecutionError> = readBytes(
    this.store,
    memory,
    buffer,
    memoryPointer,
    ULong.SIZE_BYTES,
    bufferPointer,
).map {
        bytes ->
    var result: ULong = 0uL
    for (i in 0 until Long.SIZE_BYTES) {
        result = result or (bytes[i].toULong() shl (Byte.SIZE_BITS * i))
    }
    result
}
