package io.github.charlietap.chasm.type

data class DefinedType(
    val recursiveType: RecursiveType,
    val recursiveTypeIndex: Int,
) {
    var typeIndex: Int = -1

    constructor(
        typeIndex: Int,
        recursiveType: RecursiveType,
        recursiveTypeIndex: Int,
    ) : this(recursiveType, recursiveTypeIndex) {
        this.typeIndex = typeIndex
    }
}
