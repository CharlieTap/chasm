package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.StructType

data class StructInstance(
    val definedType: DefinedType,
    val structType: StructType,
    val fields: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StructInstance

        if (definedType != other.definedType) return false
        if (structType != other.structType) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = definedType.hashCode()
        result = 31 * result + structType.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}
