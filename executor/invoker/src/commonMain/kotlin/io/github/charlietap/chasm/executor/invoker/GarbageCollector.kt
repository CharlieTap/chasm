package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.invoker.ext.sizeInBytes
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

typealias GarbageCollector = (Store, ValueStack?) -> Result<Unit, InvocationError>

fun GarbageCollector(
    store: Store,
    stack: ValueStack?,
): Result<Unit, InvocationError> {

    val heap = store.heap
    val markedStructs = mutableSetOf<Int>()
    val markedArrays = mutableSetOf<Int>()

    stack?.iterator()?.forEach { value ->
        markReferences(value, markedStructs, markedArrays, store)
    }

    for (global in store.globals) {
        markReferences(global.value, markedStructs, markedArrays, store)
    }

    for (table in store.tables) {
        for (element in table.elements) {
            markReferences(element, markedStructs, markedArrays, store)
        }
    }

    for (segment in store.elements) {
        for (element in segment.elements) {
            markReferences(element, markedStructs, markedArrays, store)
        }
    }

    for (i in store.arrays.indices) {
        if (i !in markedArrays) {
            heap.sizeInBytes -= store.arrays[i]?.sizeInBytes ?: 0
            if (store.arrays[i] != null) {
                heap.arrayReferencePool.addFirst(i)
                store.arrays[i] = null
            }
        }
    }

    for (i in store.structs.indices) {
        if (i !in markedStructs) {
            heap.sizeInBytes -= store.structs[i]?.sizeInBytes ?: 0
            if (store.structs[i] != null) {
                heap.structReferencePool.addFirst(i)
                store.structs[i] = null
            }
        }
    }

    return Ok(Unit)
}

private fun markReferences(
    value: Long,
    markedStructs: MutableSet<Int>,
    markedArrays: MutableSet<Int>,
    store: Store,
) {
    val typeId = value and RV_TYPE_MASK
    val address = (value shr RV_SHIFT_BITS).toInt()

    when (typeId) {
        RV_TYPE_STRUCT -> {
            if (address !in markedStructs) {
                markedStructs.add(address)

                if (address < 0 || address >= store.structs.size) return
                val structInstance = store.structs[address] ?: return

                for (fieldValue in structInstance.fields) {
                    markReferences(fieldValue, markedStructs, markedArrays, store)
                }
            }
        }
        RV_TYPE_ARRAY -> {
            if (address !in markedArrays) {
                markedArrays.add(address)

                if (address < 0 || address >= store.arrays.size) return
                val arrayInstance = store.arrays[address] ?: return

                for (element in arrayInstance.fields) {
                    markReferences(element, markedStructs, markedArrays, store)
                }
            }
        }
        RV_TYPE_EXTERN -> {
            val innerRef = value shr RV_SHIFT_BITS
            markReferences(innerRef, markedStructs, markedArrays, store)
        }
        else -> Unit
    }
}
