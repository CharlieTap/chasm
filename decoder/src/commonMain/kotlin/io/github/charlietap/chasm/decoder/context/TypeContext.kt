package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.type.DefinedType

internal interface TypeContext {
    val types: MutableList<Type>
    val definedTypes: MutableList<DefinedType>
}

internal data class TypeContextImpl(
    override val types: MutableList<Type> = mutableListOf(),
    override val definedTypes: MutableList<DefinedType> = mutableListOf(),
) : TypeContext
