package io.github.charlietap.chasm.ast.type

data class GlobalType(
    val valueType: ValueType,
    val mutability: Mutability,
) : Type
