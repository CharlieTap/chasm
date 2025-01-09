package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class ElementInstance(
    var type: ReferenceType,
    var elements: Array<ReferenceValue>,
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
