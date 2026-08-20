package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

class Memory internal constructor(
    internal val reference: ExternalValue.Memory,
    store: Store,
) : Importable(store) {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Memory ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
