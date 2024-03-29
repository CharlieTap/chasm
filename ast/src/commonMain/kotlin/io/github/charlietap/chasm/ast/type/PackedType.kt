package io.github.charlietap.chasm.ast.type

import kotlin.jvm.JvmInline

sealed interface PackedType : Type {

    @JvmInline
    value class I8(val value: Byte) : PackedType

    @JvmInline
    value class I16(val value: Short) : PackedType
}
