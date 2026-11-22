package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.HostExterns
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.host.HostReferences
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

private const val NULL_ROOT_SLOT = -1
private const val CLOSED_ROOT_SLOT = -2

class ExternRef internal constructor(
    private val owner: Store,
    root: HostReferenceRoot,
    private val references: HostReferences = owner.store.heap,
    private val externs: HostExterns = owner.store.heap,
) : AutoCloseable {

    private var rootSlot = root.slot

    fun asValue(): ReferenceValue {

        checkUsable()

        if (rootSlot == NULL_ROOT_SLOT) {
            return ReferenceValue.Null(AbstractHeapType.Extern)
        }

        return references.reference(HostReferenceRoot(rootSlot)).toReferenceValue()
    }

    fun hostValue(): Any? {

        checkUsable()

        if (rootSlot == NULL_ROOT_SLOT) {
            return null
        }

        val reference = HostExternReference(references.reference(HostReferenceRoot(rootSlot)))
        return externs.value(reference)
    }

    override fun close() {
        val slot = rootSlot
        if (slot == CLOSED_ROOT_SLOT) return

        rootSlot = CLOSED_ROOT_SLOT
        if (slot >= 0 && !owner.isDropped) {
            references.release(HostReferenceRoot(slot))
        }
    }

    private fun checkUsable() {
        check(rootSlot != CLOSED_ROOT_SLOT) { "Extern reference has been closed" }
        check(!owner.isDropped) { "Extern reference belongs to a dropped store" }
    }

    internal companion object {
        fun nullExternRef(store: Store) = ExternRef(store, HostReferenceRoot(NULL_ROOT_SLOT))
    }
}

inline fun <reified T : Any> ExternRef.hostValueAs(): T? {
    return when (val value = hostValue()) {
        null -> null
        is T -> value
        else -> throw IllegalArgumentException("Expected extern host value ${T::class} but found ${value::class}")
    }
}
