package io.github.charlietap.chasm.config

import kotlin.jvm.JvmInline

@JvmInline
value class Bytes(val value: Long) {

    init {
        require(value >= 0) { "a size must not be negative" }
    }

    override fun toString(): String {
        return when {
            value != 0L && value % GB == 0L -> "${value / GB} GB"
            value != 0L && value % MB == 0L -> "${value / MB} MB"
            value != 0L && value % KB == 0L -> "${value / KB} KB"
            else -> "$value bytes"
        }
    }

    companion object {
        private const val KB = 1024L
        private const val MB = KB * 1024L
        private const val GB = MB * 1024L
        const val PAGE: Long = 64L * KB
    }
}

val Int.bytes: Bytes get() = Bytes(toLong())

val Int.KB: Bytes get() = Bytes(toLong() * 1024L)

val Int.MB: Bytes get() = Bytes(toLong() * 1024L * 1024L)

val Int.GB: Bytes get() = Bytes(toLong() * 1024L * 1024L * 1024L)

val Int.pages: Bytes get() = Bytes(toLong() * Bytes.PAGE)
