package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference

fun runtimeType(
    instance: Instance,
    typeIndex: Int,
): HostGcType {
    return instance.engine.runtimeType(instance, typeIndex)
}

fun structFieldCount(
    store: Store,
    type: HostGcType,
): Int {
    return store.engine.structFieldCount(type)
}

fun structFieldInfo(
    store: Store,
    type: HostGcType,
    fieldIndex: Int,
): HostGcFieldInfo {
    return store.engine.structFieldInfo(type, fieldIndex)
}

fun arrayElementInfo(
    store: Store,
    type: HostGcType,
): HostGcFieldInfo {
    return store.engine.arrayElementInfo(type)
}

fun isSubtype(
    store: Store,
    actual: HostGcType,
    expected: HostGcType,
): Boolean {
    return store.engine.isSubtype(actual, expected)
}

fun allocateStruct(
    store: Store,
    type: HostGcType,
    fields: LongArray,
    references: Array<Any?>,
): StructReference {
    return store.engine.allocateStruct(type, fields, references)
}

@UnsafeHostApi
fun unsafeAllocateStruct(
    store: Store,
    type: HostGcType,
    fields: LongArray,
    references: Array<Any?>,
): StructReference {
    return store.engine.unsafeAllocateStruct(type, fields, references)
}

fun allocateArray(
    store: Store,
    type: HostGcType,
    length: Int,
    initialValue: Long,
): ArrayReference {
    return store.engine.allocateArray(type, length, initialValue)
}

fun allocateArray(
    store: Store,
    type: HostGcType,
    length: Int,
    initialReference: Any?,
): ArrayReference {
    return store.engine.allocateArray(type, length, initialReference)
}

fun allocateArray(
    store: Store,
    type: HostGcType,
    elements: LongArray,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return store.engine.allocateArray(type, elements, elementOffset, elementCount)
}

fun allocateArray(
    store: Store,
    type: HostGcType,
    elements: Array<Any?>,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return store.engine.allocateArray(type, elements, elementOffset, elementCount)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: ByteArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: CharArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: IntArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: LongArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: FloatArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: DoubleArray,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: Array<Any?>,
): ArrayReference {
    return store.engine.unsafeAllocateArray(type, storage)
}
