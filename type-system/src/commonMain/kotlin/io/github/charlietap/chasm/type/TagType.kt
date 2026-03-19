package io.github.charlietap.chasm.type

data class TagType(
    val attribute: Attribute,
    val typeIndex: Int,
    var functionType: FunctionType,
) {
    sealed interface Attribute {
        data object Exception : Attribute
    }
}
