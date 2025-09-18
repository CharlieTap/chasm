package io.github.charlietap.chasm.type

data class TagType(
    val attribute: Attribute,
    val definedType: DefinedType,
    val functionType: FunctionType,
) {
    sealed interface Attribute {
        data object Exception : Attribute
    }
}
