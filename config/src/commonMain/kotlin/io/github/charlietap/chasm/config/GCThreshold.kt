package io.github.charlietap.chasm.config

import kotlin.jvm.JvmInline

sealed interface GCThreshold {

    val bytes: Long

    @JvmInline
    value class KB(val value: Int) : GCThreshold {
        override val bytes: Long
            get() = value * 1024L
    }

    @JvmInline
    value class MB(val value: Int) : GCThreshold {
        override val bytes: Long
            get() = value * 1024L * 1024L
    }
}
