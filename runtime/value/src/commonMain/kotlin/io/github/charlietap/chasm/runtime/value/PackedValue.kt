package io.github.charlietap.chasm.runtime.value

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
sealed interface PackedValue {
    @JvmInline
    value class I8(val value: Long) : PackedValue

    @JvmInline
    value class I16(val value: Long) : PackedValue
}
