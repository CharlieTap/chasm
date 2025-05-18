package io.github.charlietap.chasm.executor.invoker.ext

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.runtime.store.Store

internal fun Store.allocateArray(
    instance: ArrayInstance,
): Address.Array {

    val reference = heap.arrayReferencePool.removeFirstOrNull()
    if (reference != null) {
        arrays[reference] = instance
    } else {
        arrays.add(instance)
    }

    heap.sizeInBytes += instance.sizeInBytes
    return Address.Array(reference ?: (arrays.size - 1))
}

internal fun Store.allocateStruct(
    instance: StructInstance,
): Address.Struct {

    val reference = heap.structReferencePool.removeFirstOrNull()
    if (reference != null) {
        structs[reference] = instance
    } else {
        structs.add(instance)
    }

    heap.sizeInBytes += instance.sizeInBytes
    return Address.Struct(reference ?: (structs.size - 1))
}
