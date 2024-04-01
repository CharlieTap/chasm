package io.github.charlietap.chasm.ast.type

sealed interface PackedType : Type {

    data object I8 : PackedType

    data object I16 : PackedType
}
