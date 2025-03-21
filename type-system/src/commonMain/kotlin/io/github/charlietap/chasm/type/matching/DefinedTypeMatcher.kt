package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.rolling.substitution.DefinedTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

fun DefinedTypeMatcher(
    type1: DefinedType,
    type2: DefinedType,
    context: TypeMatcherContext,
): Boolean = DefinedTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    definedTypeSubstitutor = ::DefinedTypeSubstitutor,
    heapTypeMatcher = ::HeapTypeMatcher,
)

internal inline fun DefinedTypeMatcher(
    type1: DefinedType,
    type2: DefinedType,
    context: TypeMatcherContext,
    crossinline definedTypeSubstitutor: TypeSubstitutor<DefinedType>,
    crossinline heapTypeMatcher: TypeMatcher<HeapType>,
): Boolean = when {
    type1 === type2 -> true
    else -> {
        val substitution = context.substitution

        if (type1.recursiveType.state != RecursiveType.State.CLOSED) {
            definedTypeSubstitutor(type1, substitution)
        }

        if (type2.recursiveType.state != RecursiveType.State.CLOSED) {
            definedTypeSubstitutor(type2, substitution)
        }

        if (type1 == type2) {
            true
        } else {
            val subType = context.unroller(type1)
            subType.superTypes.any { ht1 ->
                heapTypeMatcher(ht1, ConcreteHeapType.Defined(type2), context)
            }
        }
    }
}
