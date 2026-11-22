package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference

fun arrayType(array: ArrayReference): HostGcType {
    return array.engine().arrayType(array)
}

fun arrayLength(array: ArrayReference): Int {
    return array.engine().arrayLength(array)
}

fun readArrayElement(
    array: ArrayReference,
    index: Int,
): Long {
    return array.engine().readArrayElement(array, index)
}

fun readArrayReference(
    array: ArrayReference,
    index: Int,
): Any? {
    return array.engine().readArrayReference(array, index)
}

fun writeArrayElement(
    array: ArrayReference,
    index: Int,
    value: Long,
) {
    array.engine().writeArrayElement(array, index, value)
}

fun writeArrayReference(
    array: ArrayReference,
    index: Int,
    value: Any?,
) {
    array.engine().writeArrayReference(array, index, value)
}

fun readArrayElements(
    array: ArrayReference,
    offset: Int,
    destination: LongArray,
    destinationOffset: Int = 0,
    count: Int = destination.size - destinationOffset,
) {
    array.engine().readArrayElements(array, offset, destination, destinationOffset, count)
}

fun writeArrayElements(
    array: ArrayReference,
    offset: Int,
    source: LongArray,
    sourceOffset: Int = 0,
    count: Int = source.size - sourceOffset,
) {
    array.engine().writeArrayElements(array, offset, source, sourceOffset, count)
}

fun fillArray(
    array: ArrayReference,
    offset: Int,
    count: Int,
    value: Long,
) {
    array.engine().fillArray(array, offset, count, value)
}

fun copyArray(
    source: ArrayReference,
    sourceOffset: Int,
    destination: ArrayReference,
    destinationOffset: Int,
    count: Int,
) {
    destination.engine().copyArray(source, sourceOffset, destination, destinationOffset, count)
}

@UnsafeHostApi
fun unsafeWriteArrayReference(
    array: ArrayReference,
    index: Int,
    value: Any?,
) {
    array.engine().unsafeWriteArrayReference(array, index, value)
}
