package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

class Table internal constructor(
    internal val reference: ExternalValue.Table,
    store: Store,
) : Importable(store) {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Table ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
