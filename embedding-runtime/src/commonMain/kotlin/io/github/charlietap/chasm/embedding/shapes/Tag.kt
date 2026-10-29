package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

class Tag internal constructor(
    internal val reference: ExternalValue.Tag,
    store: Store,
) : Importable(store) {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Tag ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
