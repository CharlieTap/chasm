package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.embedding.error.ChasmError.ExecutionError
import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

private const val NULL_ROOT_SLOT = -1
private const val CLOSED_ROOT_SLOT = -2

class ExternRef internal constructor(
    private val owner: Store,
    root: HostReferenceRoot,
) : AutoCloseable {

    private var rootSlot = root.slot

    fun asValue(): ChasmResult<ReferenceValue, ExecutionError> {
        stateError()?.let { return ChasmResult.Error(it) }
        if (rootSlot == NULL_ROOT_SLOT) {
            return ChasmResult.Success(ReferenceValue.Null(AbstractHeapType.Extern))
        }

        val reference = owner.store.heap.reference(HostReferenceRoot(rootSlot))
        return ChasmResult.Success(reference.toReferenceValue())
    }

    fun hostValue(): ChasmResult<Any?, ExecutionError> {
        stateError()?.let { return ChasmResult.Error(it) }
        if (rootSlot == NULL_ROOT_SLOT) return ChasmResult.Success(null)

        val heap = owner.store.heap
        val reference = HostExternReference(heap.reference(HostReferenceRoot(rootSlot)))
        return ChasmResult.Success(heap.value(reference))
    }

    override fun close() {
        val slot = rootSlot
        if (slot == CLOSED_ROOT_SLOT) return

        rootSlot = CLOSED_ROOT_SLOT
        if (slot >= 0 && !owner.isDropped) {
            owner.store.heap.release(HostReferenceRoot(slot))
        }
    }

    private fun stateError(): ExecutionError? = when {
        rootSlot == CLOSED_ROOT_SLOT -> ExecutionError("Extern reference has been closed")
        owner.isDropped -> ExecutionError("Extern reference belongs to a dropped store")
        else -> null
    }

    internal companion object {
        fun nullExternRef(store: Store) = ExternRef(store, HostReferenceRoot(NULL_ROOT_SLOT))
    }
}

inline fun <reified T : Any> ExternRef.hostValueAs(): ChasmResult<T?, ExecutionError> =
    when (val result = hostValue()) {
        is ChasmResult.Success -> {
            val value = result.result
            if (value == null || value is T) {
                ChasmResult.Success(value)
            } else {
                ChasmResult.Error(
                    ExecutionError(
                        "Expected extern host value ${T::class} but found ${value::class}",
                    ),
                )
            }
        }

        is ChasmResult.Error -> result
    }
