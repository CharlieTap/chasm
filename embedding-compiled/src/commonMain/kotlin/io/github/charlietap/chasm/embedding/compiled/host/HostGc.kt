package io.github.charlietap.chasm.embedding.compiled.host

import io.github.charlietap.chasm.embedding.compiled.HostGcFieldInfo
import io.github.charlietap.chasm.embedding.compiled.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.embedding.compiled.allocateArray as catalogueAllocateArray
import io.github.charlietap.chasm.embedding.compiled.allocateStruct as catalogueAllocateStruct
import io.github.charlietap.chasm.embedding.compiled.arrayElementInfo as catalogueArrayElementInfo
import io.github.charlietap.chasm.embedding.compiled.collect as catalogueCollect
import io.github.charlietap.chasm.embedding.compiled.isSubtype as catalogueIsSubtype
import io.github.charlietap.chasm.embedding.compiled.runtimeType as catalogueRuntimeType
import io.github.charlietap.chasm.embedding.compiled.structFieldCount as catalogueStructFieldCount
import io.github.charlietap.chasm.embedding.compiled.structFieldInfo as catalogueStructFieldInfo
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateArray as catalogueUnsafeAllocateArray
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateStruct as catalogueUnsafeAllocateStruct

context(module: HostModuleInstance)
inline fun runtimeType(typeIndex: Int): HostGcType {
    return catalogueRuntimeType(module.instance, typeIndex)
}

context(resources: HostResources)
inline fun structFieldCount(type: HostGcType): Int {
    return catalogueStructFieldCount(resources.store, type)
}

context(resources: HostResources)
inline fun structFieldInfo(
    type: HostGcType,
    fieldIndex: Int,
): HostGcFieldInfo {
    return catalogueStructFieldInfo(resources.store, type, fieldIndex)
}

context(resources: HostResources)
inline fun arrayElementInfo(type: HostGcType): HostGcFieldInfo {
    return catalogueArrayElementInfo(resources.store, type)
}

context(resources: HostResources)
inline fun isSubtype(
    actual: HostGcType,
    expected: HostGcType,
): Boolean {
    return catalogueIsSubtype(resources.store, actual, expected)
}

context(resources: HostResources)
inline fun collect() {
    catalogueCollect(resources.store)
}

context(resources: HostResources)
inline fun allocateStruct(
    type: HostGcType,
    fields: LongArray,
    references: Array<Any?>,
): StructReference {
    return catalogueAllocateStruct(resources.store, type, fields, references)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateStruct(
    type: HostGcType,
    fields: LongArray,
    references: Array<Any?>,
): StructReference {
    return catalogueUnsafeAllocateStruct(resources.store, type, fields, references)
}

context(resources: HostResources)
inline fun allocateArray(
    type: HostGcType,
    length: Int,
    initialValue: Long,
): ArrayReference {
    return catalogueAllocateArray(resources.store, type, length, initialValue)
}

context(resources: HostResources)
inline fun allocateArray(
    type: HostGcType,
    length: Int,
    initialReference: Any?,
): ArrayReference {
    return catalogueAllocateArray(resources.store, type, length, initialReference)
}

context(resources: HostResources)
inline fun allocateArray(
    type: HostGcType,
    elements: LongArray,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return catalogueAllocateArray(resources.store, type, elements, elementOffset, elementCount)
}

context(resources: HostResources)
inline fun allocateArray(
    type: HostGcType,
    elements: Array<Any?>,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return catalogueAllocateArray(resources.store, type, elements, elementOffset, elementCount)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: ByteArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: CharArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: IntArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: LongArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: FloatArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: DoubleArray,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}

@UnsafeHostApi
context(resources: HostResources)
inline fun unsafeAllocateArray(
    type: HostGcType,
    storage: Array<Any?>,
): ArrayReference {
    return catalogueUnsafeAllocateArray(resources.store, type, storage)
}
