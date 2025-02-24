package io.github.charlietap.chasm.type

import kotlin.jvm.JvmInline

sealed interface BlockType {
    data object Empty : BlockType

    @JvmInline
    value class ValType(val valueType: ValueType) : BlockType

    @JvmInline
    value class SignedTypeIndex(val typeIndex: Int) : BlockType
}
