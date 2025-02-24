package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.type.rolling.substitution.RecursiveTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias TypeAllocator = (InstantiationContext, List<RecursiveType>) -> List<DefinedType>

internal inline fun TypeAllocator(
    context: InstantiationContext,
    types: List<RecursiveType>,
): List<DefinedType> = TypeAllocator(
    context = context,
    types = types,
    recursiveTypeSubstitutor = ::RecursiveTypeSubstitutor,
    definedTypeRoller = ::DefinedTypeRoller,
)

internal inline fun TypeAllocator(
    context: InstantiationContext,
    types: List<RecursiveType>,
    recursiveTypeSubstitutor: TypeSubstitutor<RecursiveType>,
    definedTypeRoller: DefinedTypeRoller,
): List<DefinedType> {
    return types.flatMap { type ->
        // This closes the recursive type in the sense it replaces its TypeIndex's with defined types
        // preventing the defined type roller replacing them with RecursiveTypeIndex's which cannot be
        // used across module boundaries
        val substituted = recursiveTypeSubstitutor(type, context.substitutor)
        val definedTypes = definedTypeRoller(context.types.size, substituted)
        context.types += definedTypes
        definedTypes
    }
}
