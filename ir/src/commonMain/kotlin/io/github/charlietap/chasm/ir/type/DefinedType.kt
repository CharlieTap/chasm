package io.github.charlietap.chasm.ir.type

data class DefinedType(
    var recursiveType: RecursiveType,
    val recursiveTypeIndex: Int,
)
