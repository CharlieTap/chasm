package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.DefinedType

data class ArrayInstance(
    val definedType: DefinedType,
    val arrayType: ArrayType,
    val fields: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ArrayInstance

        if (definedType != other.definedType) return false
        if (arrayType != other.arrayType) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = definedType.hashCode()
        result = 31 * result + arrayType.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}
