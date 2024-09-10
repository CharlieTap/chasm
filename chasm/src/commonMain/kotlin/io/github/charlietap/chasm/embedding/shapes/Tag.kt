package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

class Tag internal constructor(internal val reference: ExternalValue.Tag) : Importable {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Tag ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
