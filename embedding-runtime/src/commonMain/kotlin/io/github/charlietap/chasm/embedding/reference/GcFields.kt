package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal fun RuntimeStore.structFieldType(
    reference: Long,
    index: Int,
): FieldType {

    val gc = referenceBridge?.gc ?: return heap.structFieldType(reference, index)
    val type = gc.structType(reference)
    if (index !in 0 until gc.structFieldCount(type)) {
        throw InvocationException(InvocationError.StructFieldLookupFailed(index))
    }

    return heap.runtimeStructFieldType(type, index)
}

internal fun RuntimeStore.readStructField(
    reference: Long,
    index: Int,
): Long {
    return referenceBridge?.gc?.readStructField(reference, index) ?: heap.getStructField(reference, index)
}

internal fun RuntimeStore.writeStructField(
    reference: Long,
    index: Int,
    value: Long,
) {
    referenceBridge?.gc?.writeStructField(reference, index, value) ?: heap.setStructField(reference, index, value)
}

internal fun RuntimeStore.arrayFieldType(
    reference: Long,
): FieldType {
    val gc = referenceBridge?.gc ?: return heap.arrayFieldType(reference)
    return heap.runtimeArrayFieldType(gc.arrayType(reference))
}

internal fun RuntimeStore.arrayLength(
    reference: Long,
): Int {
    return referenceBridge?.gc?.arrayLength(reference) ?: heap.arrayLengthChecked(reference)
}

internal fun RuntimeStore.readArrayElement(
    reference: Long,
    index: Int,
): Long {
    val gc = referenceBridge?.gc ?: return heap.getArrayElement(reference, index)
    if (index !in 0 until gc.arrayLength(reference)) {
        throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
    }

    return gc.readArrayElement(reference, index)
}

internal fun RuntimeStore.writeArrayElement(
    reference: Long,
    index: Int,
    value: Long,
) {
    referenceBridge?.gc?.writeArrayElement(reference, index, value) ?: heap.setArrayElement(reference, index, value)
}
