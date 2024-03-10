package io.github.charlietap.chasm.executor.runtime.instance

data class DataInstance(
    val bytes: UByteArray,
    val dropped: Boolean = false,
) {
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
