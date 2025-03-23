package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

fun DefinedTypeMatcher(
    type1: DefinedType,
    type2: DefinedType,
    context: TypeMatcherContext,
): Boolean = DefinedTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    heapTypeMatcher = ::HeapTypeMatcher,
)

internal inline fun DefinedTypeMatcher(
    type1: DefinedType,
    type2: DefinedType,
    context: TypeMatcherContext,
    crossinline heapTypeMatcher: TypeMatcher<HeapType>,
): Boolean = when {
    type1 === type2 -> true
    type1 == type2 -> true
    else -> {
        val subType = context.unroller(type1)
        subType.superTypes.any { ht1 ->
            heapTypeMatcher(ht1, ConcreteHeapType.Defined(type2), context)
        }
    }
}
