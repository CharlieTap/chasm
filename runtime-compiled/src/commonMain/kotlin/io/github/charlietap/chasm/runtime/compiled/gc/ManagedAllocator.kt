package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.execution.ARRAY_OVERHEAD
import io.github.charlietap.chasm.runtime.store.Store
import java.lang.reflect.Array as ReflectArray

@InternalChasmApi
class ManagedAllocator(
    private val store: Store,
) {

    fun newStruct(layout: StructLayout): CompiledStruct {

        chargeAllocation(store, layout.storageLayout.bytes)

        return layout.storageLayout.newStruct(layout.rtt)
    }

    fun newStorage(
        layout: ArrayLayout,
        length: Int,
    ): Any {

        chargeStorage(layout, length)

        return layout.storageLayout.newStorage(length)
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
        return layout.storageLayout.newArray(layout.rtt, storage)
    }

    private fun chargeStorage(
        layout: ArrayLayout,
        length: Int,
    ) {

        require(length >= 0) { "negative array length $length" }

        checkArrayLength(length, layout.storageLayout.element.shift)
        chargeAllocation(store, (length shl layout.storageLayout.element.shift) + ARRAY_OVERHEAD)
    }
}
