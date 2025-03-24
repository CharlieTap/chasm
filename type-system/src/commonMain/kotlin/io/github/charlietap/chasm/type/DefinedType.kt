package io.github.charlietap.chasm.type

import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller

data class DefinedType(
    val recursiveType: RecursiveType,
    val recursiveTypeIndex: Int,
) {
    var typeIndex: Int = -1
    val asSubType by lazy {
        DefinedTypeUnroller(this)
    }

    constructor(
        typeIndex: Int,
        recursiveType: RecursiveType,
        recursiveTypeIndex: Int,
    ) : this(recursiveType, recursiveTypeIndex) {
        this.typeIndex = typeIndex
    }
}
