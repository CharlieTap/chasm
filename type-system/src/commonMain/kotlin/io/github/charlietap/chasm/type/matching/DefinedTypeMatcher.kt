package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
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
    definedTypeUnroller = ::DefinedTypeUnroller,
    heapTypeMatcher = ::HeapTypeMatcher,
)

internal inline fun DefinedTypeMatcher(
    type1: DefinedType,
    type2: DefinedType,
    context: TypeMatcherContext,
    crossinline definedTypeSubstitutor: TypeSubstitutor<DefinedType>,
    crossinline definedTypeUnroller: DefinedTypeUnroller,
    crossinline heapTypeMatcher: TypeMatcher<HeapType>,
): Boolean = when {
    type1 === type2 -> true
    else -> {
        val substitution = context.substitution()
        if (definedTypeSubstitutor(type1, substitution) == definedTypeSubstitutor(type2, substitution)) {
            true
        } else {
            val subType = definedTypeUnroller(type1)
            subType.superTypes.any { ht1 ->
                heapTypeMatcher(ht1, ConcreteHeapType.Defined(type2), context)
            }
        }
    }
}
