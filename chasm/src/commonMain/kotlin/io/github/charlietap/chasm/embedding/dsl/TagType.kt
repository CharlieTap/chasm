package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TagType

class TagTypeBuilder {

    lateinit var attribute: TagType.Attribute
    private lateinit var functionType: FunctionType

    fun functionType(builder: FunctionTypeBuilder.() -> Unit) {
        functionType = FunctionTypeBuilder().apply(builder).build()
    }

    fun build() = TagType(
        attribute,
        DefinedType(
            RecursiveType(
                subTypes = listOf(
                    SubType.Final(emptyList(), CompositeType.Function(functionType)),
                ),
                state = RecursiveType.State.SYNTAX,
            ),
            0,
        ),
        functionType,
    )
}
