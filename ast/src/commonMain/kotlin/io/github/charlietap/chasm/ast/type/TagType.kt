package io.github.charlietap.chasm.ast.type

import io.github.charlietap.chasm.ast.module.Index

data class TagType(
    val attribute: Attribute,
    val index: Index.TypeIndex,
) {
    sealed interface Attribute {
        data object Exception : Attribute
    }
}
