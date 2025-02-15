package io.github.charlietap.chasm.ir.type

sealed interface PackedType : Type {

    data object I8 : PackedType

    data object I16 : PackedType
}
