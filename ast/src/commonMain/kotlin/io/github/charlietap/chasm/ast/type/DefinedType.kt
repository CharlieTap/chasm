package io.github.charlietap.chasm.ast.type

data class DefinedType(
    var recursiveType: RecursiveType,
    val recursiveTypeIndex: Int,
)
