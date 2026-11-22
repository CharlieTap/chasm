package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostGcFieldInfo
import io.github.charlietap.chasm.host.HostGcType
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

fun allocateNumericArray(
    store: Store,
    type: HostGcType,
    length: Int,
    initialValue: Long,
): ArrayReference {
    return store.engine.allocateNumericArray(type, length, initialValue)
}

fun allocateNumericArray(
    store: Store,
    type: HostGcType,
    elements: LongArray,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return store.engine.allocateNumericArray(type, elements, elementOffset, elementCount)
}

fun allocateReferenceArray(
    store: Store,
    type: HostGcType,
    length: Int,
    initialReference: Any?,
): ArrayReference {
    return store.engine.allocateReferenceArray(type, length, initialReference)
}

fun allocateReferenceArray(
    store: Store,
    type: HostGcType,
    elements: Array<Any?>,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return store.engine.allocateReferenceArray(type, elements, elementOffset, elementCount)
}
