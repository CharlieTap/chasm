package io.github.charlietap.chasm.type

data class GlobalType(
    var valueType: ValueType,
    val mutability: Mutability,
) : Type
