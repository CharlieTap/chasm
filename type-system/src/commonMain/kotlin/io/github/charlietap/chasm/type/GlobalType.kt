package io.github.charlietap.chasm.type

data class GlobalType(
    val valueType: ValueType,
    val mutability: Mutability,
) : Type
