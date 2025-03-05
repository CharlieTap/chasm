package io.github.charlietap.chasm.runtime.instance

data class DataInstance(
    var bytes: UByteArray,
) {
    companion object {
        val EMPTY = UByteArray(0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DataInstance

        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
