package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType

data class ElementInstance(
    var type: ReferenceType,
    var elements: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ElementInstance

        if (type != other.type) return false
        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + elements.contentHashCode()
        return result
    }
}
