package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

class Table internal constructor(internal val reference: ExternalValue.Table) : Importable {
    override fun equals(other: Any?): Boolean {
        val otherFunction = other as? Table ?: return false
        return this.reference == otherFunction.reference
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}
