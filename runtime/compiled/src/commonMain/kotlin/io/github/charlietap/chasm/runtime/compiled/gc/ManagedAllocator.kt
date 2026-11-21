package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD
import io.github.charlietap.chasm.runtime.store.Store
import java.lang.reflect.Array as ReflectArray

class ManagedAllocator(
    private val store: Store,
) {

    fun newStruct(layout: StructLayout): ManagedStruct {

        chargeAllocation(store, layout.bytes)

        return layout.constructor.newInstance() as ManagedStruct
    }

    fun newStorage(
        layout: ArrayLayout,
        length: Int,
    ): Any {

        chargeStorage(layout, length)

        return ReflectArray.newInstance(layout.storage.componentType, length)
    }

    fun adoptStorage(
        layout: ArrayLayout,
        storage: Any,
    ) {
        chargeStorage(layout, ReflectArray.getLength(storage))
    }

    fun newArray(
        layout: ArrayLayout,
        storage: Any,
    ): ManagedArray {
        return layout.constructor.newInstance(storage) as ManagedArray
    }

    private fun chargeStorage(
        layout: ArrayLayout,
        length: Int,
    ) {

        require(length >= 0) { "negative array length $length" }

        checkArrayLength(length, layout.shift)
        chargeAllocation(store, (length shl layout.shift) + ARRAY_OVERHEAD)
    }
}
