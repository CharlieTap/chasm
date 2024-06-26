package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class TableInstance(
    var type: TableType,
    var elements: Array<ReferenceValue>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TableInstance

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
