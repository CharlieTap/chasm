package io.github.charlietap.chasm.type

data class TagType(
    val attribute: Attribute,
    val type: FunctionType,
) {
    sealed interface Attribute {
        data object Exception : Attribute
    }
}
