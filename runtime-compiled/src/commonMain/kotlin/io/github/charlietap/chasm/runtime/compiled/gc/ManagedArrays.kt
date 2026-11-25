@file:JvmName("ManagedArrays")

package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.DataInstance
import kotlin.jvm.JvmName

@InternalChasmApi
fun arrayLength(
    array: ManagedArray,
): Int {
    return array.length()
}

@InternalChasmApi
fun copyArray(
    destination: Any?,
    destinationOffset: Int,
    source: Any?,
    sourceOffset: Int,
    count: Int,
) {
    val target = destination.array()
    val origin = source.array()
    checkRange(target, destinationOffset, count)
    checkRange(origin, sourceOffset, count)
    System.arraycopy(origin.storage(), sourceOffset, target.storage(), destinationOffset, count)
}

@InternalChasmApi
fun fillInts(
    array: Any?,
    offset: Int,
    value: Int,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as IntArray).fill(value, offset, offset + count)
}

@InternalChasmApi
fun fillLongs(
    array: Any?,
    offset: Int,
    value: Long,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as LongArray).fill(value, offset, offset + count)
}

@InternalChasmApi
fun fillFloats(
    array: Any?,
    offset: Int,
    value: Float,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as FloatArray).fill(value, offset, offset + count)
}

@InternalChasmApi
fun fillDoubles(
    array: Any?,
    offset: Int,
    value: Double,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as DoubleArray).fill(value, offset, offset + count)
}

@InternalChasmApi
fun fillBytes(
    array: Any?,
    offset: Int,
    value: Int,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as ByteArray).fill(value.toByte(), offset, offset + count)
}

@InternalChasmApi
fun fillChars(
    array: Any?,
    offset: Int,
    value: Int,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    (target.storage() as CharArray).fill(value.toChar(), offset, offset + count)
}

@InternalChasmApi
fun fillObjects(
    array: Any?,
    offset: Int,
    value: Any?,
    count: Int,
) {
    val target = array.array()
    checkRange(target, offset, count)
    @Suppress("UNCHECKED_CAST")
    (target.storage() as Array<Any?>).fill(value, offset, offset + count)
}

@InternalChasmApi
fun checkDataRange(
    data: DataInstance,
    offset: Int,
    count: Int,
    width: Int,
) {
    if (offset.toUInt().toLong() + count.toUInt().toLong() * width > data.bytes.size) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }
}

@InternalChasmApi
fun checkElementRange(
    segment: ReferenceSegment,
    offset: Int,
    count: Int,
) {
    if (offset.toUInt().toLong() + count.toUInt().toLong() > segment.elements.size) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}

@InternalChasmApi
fun copyData(
    data: DataInstance,
    offset: Int,
    target: Any,
    targetOffset: Int,
    count: Int,
) {
    val bytes = data.bytes
    when (target) {
        is ByteArray -> for (index in 0 until count) target[targetOffset + index] = bytes[offset + index].toByte()
        is CharArray -> for (index in 0 until count) target[targetOffset + index] = littleEndian(bytes, offset + index * 2, 2).toInt().toChar()
        is IntArray -> for (index in 0 until count) target[targetOffset + index] = littleEndian(bytes, offset + index * 4, 4).toInt()
        is FloatArray -> for (index in 0 until count) target[targetOffset + index] = Float.fromBits(littleEndian(bytes, offset + index * 4, 4).toInt())
        is LongArray -> for (index in 0 until count) target[targetOffset + index] = littleEndian(bytes, offset + index * 8, 8)
        is DoubleArray -> for (index in 0 until count) target[targetOffset + index] = Double.fromBits(littleEndian(bytes, offset + index * 8, 8))
        else -> throw UnsupportedOperationException("data cannot initialise ${target::class.simpleName}")
    }
}

@InternalChasmApi
fun copyElements(
    segment: ReferenceSegment,
    offset: Int,
    target: Array<Any?>,
    targetOffset: Int,
    count: Int,
) {
    segment.elements.copyInto(target, targetOffset, offset, offset + count)
}

@InternalChasmApi
fun initArrayData(
    array: Any?,
    destinationOffset: Int,
    data: DataInstance,
    sourceOffset: Int,
    count: Int,
    width: Int,
) {
    val target = array.array()
    checkRange(target, destinationOffset, count)
    checkDataRange(data, sourceOffset, count, width)
    copyData(data, sourceOffset, target.storage(), destinationOffset, count)
}

@InternalChasmApi
fun initArrayElements(
    array: Any?,
    destinationOffset: Int,
    segment: ReferenceSegment,
    sourceOffset: Int,
    count: Int,
) {
    val target = array.array()
    checkRange(target, destinationOffset, count)
    checkElementRange(segment, sourceOffset, count)
    @Suppress("UNCHECKED_CAST")
    copyElements(segment, sourceOffset, target.storage() as Array<Any?>, destinationOffset, count)
}

private fun littleEndian(
    bytes: UByteArray,
    offset: Int,
    width: Int,
): Long {
    var value = 0L
    for (index in width - 1 downTo 0) value = (value shl 8) or bytes[offset + index].toLong()
    return value
}

private fun Any?.array(): ManagedArray {
    return this as? ManagedArray ?: throw InvocationException(InvocationError.NonNullReferenceExpected)
}

private fun checkRange(
    array: ManagedArray,
    offset: Int,
    count: Int,
) {
    if (offset.toUInt().toLong() + count.toUInt().toLong() > array.length()) {
        throw InvocationException(InvocationError.ArrayOperationOutOfBounds)
    }
}
