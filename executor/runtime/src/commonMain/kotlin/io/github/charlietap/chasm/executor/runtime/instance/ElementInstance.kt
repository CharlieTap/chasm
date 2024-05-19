package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class ElementInstance(
    val type: ReferenceType,
    val elements: Array<ReferenceValue>,
    val dropped: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ElementInstance

        if (type != other.type) return false
        if (!elements.contentEquals(other.elements)) return false
        if (dropped != other.dropped) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + elements.contentHashCode()
        result = 31 * result + dropped.hashCode()
        return result
    }
}
