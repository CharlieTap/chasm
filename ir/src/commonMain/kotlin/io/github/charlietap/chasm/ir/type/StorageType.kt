package io.github.charlietap.chasm.ir.type

import kotlin.jvm.JvmInline

sealed interface StorageType : Type {

    @JvmInline
    value class Value(val type: ValueType) : StorageType

    @JvmInline
    value class Packed(val type: PackedType) : StorageType
}
