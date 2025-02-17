package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.RecursiveType
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
        val substitution = context.substitutor

        val closedType1 = if (type1.recursiveType.state != RecursiveType.STATE_CLOSED) {
            definedTypeSubstitutor(type1, substitution)
        } else {
            type1
        }

        val closedType2 = if (type2.recursiveType.state != RecursiveType.STATE_CLOSED) {
            definedTypeSubstitutor(type2, substitution)
        } else {
            type2
        }

        if (closedType1 == closedType2) {
            true
        } else {
            val subType = context.unroller(closedType1)
            subType.superTypes.any { ht1 ->
                heapTypeMatcher(ht1, ConcreteHeapType.Defined(closedType2), context)
            }
        }
    }
}
