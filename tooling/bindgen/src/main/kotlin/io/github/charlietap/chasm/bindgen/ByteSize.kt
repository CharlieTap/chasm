package io.github.charlietap.chasm.bindgen

@JvmInline
value class ByteSize(
    val bytes: Long,
) : Comparable<ByteSize> {
    init {
        require(bytes >= 0) { "A size cannot be negative" }
    }

    override fun compareTo(
        other: ByteSize,
    ): Int = bytes.compareTo(other.bytes)

    override fun toString(): String =
        when {
            bytes >= GB && bytes % GB == 0L -> "${bytes / GB} GB"
            bytes >= MB && bytes % MB == 0L -> "${bytes / MB} MB"
            bytes >= KB && bytes % KB == 0L -> "${bytes / KB} KB"
            else -> "$bytes B"
        }

    companion object {
        internal const val KB: Long = 1024
        internal const val MB: Long = 1024 * KB
        internal const val GB: Long = 1024 * MB

        val WASM_PAGE: ByteSize = ByteSize(64 * KB)
    }
}

val Int.bytes: ByteSize get() = ByteSize(toLong())

val Int.KB: ByteSize get() = ByteSize(toLong() * ByteSize.KB)

val Int.MB: ByteSize get() = ByteSize(toLong() * ByteSize.MB)

val Int.GB: ByteSize get() = ByteSize(toLong() * ByteSize.GB)

val Int.wasmPages: ByteSize get() = ByteSize(toLong() * ByteSize.WASM_PAGE.bytes)
