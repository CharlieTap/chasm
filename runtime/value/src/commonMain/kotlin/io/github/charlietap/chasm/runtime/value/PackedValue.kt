package io.github.charlietap.chasm.runtime.value

import kotlin.jvm.JvmInline

sealed interface PackedValue {
    @JvmInline
    value class I8(val value: Long) : PackedValue

    @JvmInline
    value class I16(val value: Long) : PackedValue
}
