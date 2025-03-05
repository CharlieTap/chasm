package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address

data class ExceptionInstance(
    val tagAddress: Address.Tag,
    var fields: LongArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExceptionInstance

        if (tagAddress != other.tagAddress) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tagAddress.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}
