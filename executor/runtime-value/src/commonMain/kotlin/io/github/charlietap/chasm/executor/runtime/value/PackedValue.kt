package io.github.charlietap.chasm.executor.runtime.value

import kotlin.jvm.JvmInline

sealed interface PackedValue {
    @JvmInline
    value class I8(val value: UByte) : PackedValue

    @JvmInline
    value class I16(val value: UShort) : PackedValue
}
