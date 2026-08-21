package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

internal typealias Caster = (Long, ReferenceTypeTest, Store) -> Boolean

internal inline fun Caster(
    referenceValue: Long,
    typeTest: ReferenceTypeTest,
    store: Store,
): Boolean {

    val referenceTag = (referenceValue and RV_TYPE_MASK).toInt()
    if (referenceTag == RV_TYPE_NULL.toInt()) {
        return typeTest.nullable
    }

    return if (typeTest.isDefined) {
        referenceValue.isInstanceOf(referenceTag, typeTest.rtt, store)
    } else {
        typeTest.acceptedReferenceTags and (1 shl referenceTag) != 0
    }
}

private fun Long.isInstanceOf(
    referenceTag: Int,
    castRuntimeType: RTT,
    store: Store,
): Boolean {
    return when (referenceTag) {
        RV_TYPE_STRUCT.toInt() -> {
            val runtimeTypeId = store.heap.structRuntimeTypeIdOrNegative(this)
            if (runtimeTypeId < 0) return false
            store.heap.matchesRuntimeType(RTT(runtimeTypeId), castRuntimeType)
        }
        RV_TYPE_ARRAY.toInt() -> {
            val runtimeTypeId = store.heap.arrayRuntimeTypeIdOrNegative(this)
            if (runtimeTypeId < 0) return false
            store.heap.matchesRuntimeType(RTT(runtimeTypeId), castRuntimeType)
        }
        RV_TYPE_FUNCTION.toInt() -> {
            val address = (this shr RV_SHIFT_BITS).toInt()
            val instance = store.functions.getOrNull(address) ?: return false
            store.heap.matchesRuntimeType(instance.rtt, castRuntimeType)
        }
        else -> return false
    }
}
