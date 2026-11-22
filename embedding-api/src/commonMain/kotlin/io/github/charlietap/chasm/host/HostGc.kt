package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.embedding.collect as catalogueCollect
import io.github.charlietap.chasm.embedding.reference.allocateNumericArray as catalogueAllocateNumericArray
import io.github.charlietap.chasm.embedding.reference.allocateReferenceArray as catalogueAllocateReferenceArray
import io.github.charlietap.chasm.embedding.reference.allocateStruct as catalogueAllocateStruct
import io.github.charlietap.chasm.embedding.reference.arrayElementInfo as catalogueArrayElementInfo
import io.github.charlietap.chasm.embedding.reference.isSubtype as catalogueIsSubtype
import io.github.charlietap.chasm.embedding.reference.runtimeType as catalogueRuntimeType
import io.github.charlietap.chasm.embedding.reference.structFieldCount as catalogueStructFieldCount
import io.github.charlietap.chasm.embedding.reference.structFieldInfo as catalogueStructFieldInfo
import io.github.charlietap.chasm.embedding.reference.unsafeAllocateStruct as catalogueUnsafeAllocateStruct

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
inline fun allocateNumericArray(
    type: HostGcType,
    length: Int,
    initialValue: Long,
): ArrayReference {
    return catalogueAllocateNumericArray(resources.store, type, length, initialValue)
}

context(resources: HostResources)
inline fun allocateNumericArray(
    type: HostGcType,
    elements: LongArray,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return catalogueAllocateNumericArray(resources.store, type, elements, elementOffset, elementCount)
}

context(resources: HostResources)
inline fun allocateReferenceArray(
    type: HostGcType,
    length: Int,
    initialReference: Any?,
): ArrayReference {
    return catalogueAllocateReferenceArray(resources.store, type, length, initialReference)
}

context(resources: HostResources)
inline fun allocateReferenceArray(
    type: HostGcType,
    elements: Array<Any?>,
    elementOffset: Int = 0,
    elementCount: Int = elements.size - elementOffset,
): ArrayReference {
    return catalogueAllocateReferenceArray(resources.store, type, elements, elementOffset, elementCount)
}
