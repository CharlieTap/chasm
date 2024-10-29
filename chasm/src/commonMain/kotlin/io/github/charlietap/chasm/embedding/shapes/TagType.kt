package io.github.charlietap.chasm.embedding.shapes

data class TagType(
    val attribute: Attribute,
    val type: FunctionType,
) {
    sealed interface Attribute {
        data object Exception : Attribute
    }
}
