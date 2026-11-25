package io.github.charlietap.chasm.host

import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateArray as catalogueUnsafeAllocateArray
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowElements as catalogueUnsafeBorrowElements

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

@UnsafeHostApi
inline fun Table.unsafeBorrowElements(): Array<Any?> {
    return catalogueUnsafeBorrowElements(this)
}
