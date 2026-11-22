@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference

@UnsafeHostApi
fun unsafeBorrowBytes(array: ArrayReference): ByteArray {
    return managed(array.engine()).unsafeBorrow(array, ByteArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowChars(array: ArrayReference): CharArray {
    return managed(array.engine()).unsafeBorrow(array, CharArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowInts(array: ArrayReference): IntArray {
    return managed(array.engine()).unsafeBorrow(array, IntArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowLongs(array: ArrayReference): LongArray {
    return managed(array.engine()).unsafeBorrow(array, LongArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowFloats(array: ArrayReference): FloatArray {
    return managed(array.engine()).unsafeBorrow(array, FloatArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowDoubles(array: ArrayReference): DoubleArray {
    return managed(array.engine()).unsafeBorrow(array, DoubleArray::class.java)
}

@UnsafeHostApi
fun unsafeBorrowReferences(array: ArrayReference): Array<Any?> {
    return managed(array.engine()).unsafeBorrow(array, Array<Any?>::class.java)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: ByteArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: CharArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: IntArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: LongArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: FloatArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: DoubleArray,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeAllocateArray(
    store: Store,
    type: HostGcType,
    storage: Array<Any?>,
): ArrayReference {
    return managed(store._engine()).unsafeAllocateArray(type, storage)
}

@UnsafeHostApi
fun unsafeBorrowElements(table: Table): Array<Any?> {
    return managed(table._engine()).unsafeBorrowElements(table)
}

private fun managed(engine: Engine): ManagedEngine {
    return requireNotNull(engine as? ManagedEngine) { "zero-copy access needs a store on a managed engine" }
}
