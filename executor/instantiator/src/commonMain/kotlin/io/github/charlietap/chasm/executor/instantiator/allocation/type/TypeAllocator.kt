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
        // Close all recursive types by first substituting all type indices that
        // point to external recursive types then run the DefinedTypeRoller on the result
        // which will replace all the remaining type indices which point to internal recursive
        // types with recursive type indices
        val substituted = recursiveTypeSubstitutor(type, context.substitutor)
        val definedTypes = definedTypeRoller(context.types.size, substituted)
        context.types += definedTypes
        definedTypes
    }
}
