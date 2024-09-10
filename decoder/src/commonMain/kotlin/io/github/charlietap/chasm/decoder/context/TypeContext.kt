package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal interface TypeContext {
    val types: MutableList<Type>
    val definedTypes: List<DefinedType>
}

internal data class TypeContextImpl(
    override val types: MutableList<Type> = mutableListOf(),
) : TypeContext {
    override val definedTypes: List<DefinedType> by lazy {
        DefinedTypeFactory(types.map(Type::recursiveType))
    }
}
