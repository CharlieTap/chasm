package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference

fun arrayType(array: ArrayReference): HostGcType {
    return engineOf(array).arrayType(array)
}

fun arrayLength(array: ArrayReference): Int {
    return engineOf(array).arrayLength(array)
}

fun readArrayElement(
    array: ArrayReference,
    index: Int,
): Long {
    return engineOf(array).readArrayElement(array, index)
}

fun readArrayReference(
    array: ArrayReference,
    index: Int,
): Any? {
    return engineOf(array).readArrayReference(array, index)
}

fun writeArrayElement(
    array: ArrayReference,
    index: Int,
    value: Long,
) {
    engineOf(array).writeArrayElement(array, index, value)
}

fun writeArrayReference(
    array: ArrayReference,
    index: Int,
    value: Any?,
) {
    engineOf(array).writeArrayReference(array, index, value)
}

fun readArrayElements(
    array: ArrayReference,
    offset: Int,
    destination: LongArray,
    destinationOffset: Int = 0,
    count: Int = destination.size - destinationOffset,
) {
    engineOf(array).readArrayElements(array, offset, destination, destinationOffset, count)
}

fun writeArrayElements(
    array: ArrayReference,
    offset: Int,
    source: LongArray,
    sourceOffset: Int = 0,
    count: Int = source.size - sourceOffset,
) {
    engineOf(array).writeArrayElements(array, offset, source, sourceOffset, count)
}

fun fillArray(
    array: ArrayReference,
    offset: Int,
    count: Int,
    value: Long,
) {
    engineOf(array).fillArray(array, offset, count, value)
}

fun copyArray(
    source: ArrayReference,
    sourceOffset: Int,
    destination: ArrayReference,
    destinationOffset: Int,
    count: Int,
) {
    engineOf(destination).copyArray(source, sourceOffset, destination, destinationOffset, count)
}

@UnsafeHostApi
fun unsafeWriteArrayReference(
    array: ArrayReference,
    index: Int,
    value: Any?,
) {
    engineOf(array).unsafeWriteArrayReference(array, index, value)
}

@UnsafeHostApi
fun unsafeBorrowBytes(array: ArrayReference): ByteArray {
    return engineOf(array).unsafeBorrow(array, ByteArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowChars(array: ArrayReference): CharArray {
    return engineOf(array).unsafeBorrow(array, CharArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowInts(array: ArrayReference): IntArray {
    return engineOf(array).unsafeBorrow(array, IntArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowLongs(array: ArrayReference): LongArray {
    return engineOf(array).unsafeBorrow(array, LongArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowFloats(array: ArrayReference): FloatArray {
    return engineOf(array).unsafeBorrow(array, FloatArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowDoubles(array: ArrayReference): DoubleArray {
    return engineOf(array).unsafeBorrow(array, DoubleArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowReferences(array: ArrayReference): Array<Any?> {
    return engineOf(array).unsafeBorrow(array, Array<Any?>::class.java)
}
