@file:JvmName("CompiledBufferMemory")

package io.github.charlietap.chasm.runtime.compiled

import java.nio.ByteBuffer

fun i32Load8S(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
): Int {
    return buffer.get(checkedIndex(size, address, offset, Byte.SIZE_BYTES)).toInt()
}

fun i32Load16S(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
): Int {
    return buffer.getShort(checkedIndex(size, address, offset, Short.SIZE_BYTES)).toInt()
}

fun i32Load(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
): Int {
    return buffer.getInt(checkedIndex(size, address, offset, Int.SIZE_BYTES))
}

fun i64Load(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
): Long {
    return buffer.getLong(checkedIndex(size, address, offset, Long.SIZE_BYTES))
}

fun i32Store8(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
    value: Int,
) {
    buffer.put(checkedIndex(size, address, offset, Byte.SIZE_BYTES), value.toByte())
}

fun i32Store16(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
    value: Int,
) {
    buffer.putShort(checkedIndex(size, address, offset, Short.SIZE_BYTES), value.toShort())
}

fun i32Store(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
    value: Int,
) {
    buffer.putInt(checkedIndex(size, address, offset, Int.SIZE_BYTES), value)
}

fun i64Store(
    buffer: ByteBuffer,
    size: Int,
    address: Int,
    offset: Int,
    value: Long,
) {
    buffer.putLong(checkedIndex(size, address, offset, Long.SIZE_BYTES), value)
}

private inline fun checkedIndex(
    size: Int,
    address: Int,
    offset: Int,
    bytes: Int,
): Int {

    val index = address + offset
    if ((address or index) < 0 || index > size - bytes) {
        memoryOutOfBounds()
    }

    return index
}
