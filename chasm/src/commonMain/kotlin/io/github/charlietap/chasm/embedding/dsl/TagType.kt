package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.TagType

class TagTypeBuilder {

    lateinit var attribute: TagType.Attribute
    private lateinit var functionType: FunctionType

    fun functionType(builder: FunctionTypeBuilder.() -> Unit) {
        functionType = FunctionTypeBuilder().apply(builder).build()
    }

    fun build() = TagType(
        attribute,
        functionType,
    )
}
