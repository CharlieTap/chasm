package io.github.charlietap.chasm.ir.type

data class GlobalType(
    val valueType: ValueType,
    val mutability: Mutability,
) : Type
