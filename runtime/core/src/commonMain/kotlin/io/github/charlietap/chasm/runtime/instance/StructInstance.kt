package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

data class StructInstance(
    val rtt: RTT,
    val structType: StructType,
    val fields: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as StructInstance

        if (rtt != other.rtt) return false
        if (structType != other.structType) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rtt.hashCode()
        result = 31 * result + structType.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}
