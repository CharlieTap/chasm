package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.RTT

data class ArrayInstance(
    val rtt: RTT,
    val arrayType: ArrayType,
    val fields: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ArrayInstance

        if (rtt != other.rtt) return false
        if (arrayType != other.arrayType) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rtt.hashCode()
        result = 31 * result + arrayType.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}
