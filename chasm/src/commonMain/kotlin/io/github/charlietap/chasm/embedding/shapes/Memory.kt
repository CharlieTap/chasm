package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.instance.ExternalValue

class Memory internal constructor(internal val reference: ExternalValue.Memory) : Importable {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Memory ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
