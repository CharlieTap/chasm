package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.HeapType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun ReferenceTypeMatcher(
    type1: ReferenceType,
    type2: ReferenceType,
    context: TypeMatcherContext,
): Boolean = ReferenceTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    heapTypeMatcher = ::HeapTypeMatcher,
)

internal fun ReferenceTypeMatcher(
    type1: ReferenceType,
    type2: ReferenceType,
    context: TypeMatcherContext,
    heapTypeMatcher: TypeMatcher<HeapType>,
): Boolean = when {
    type1 is ReferenceType.Ref && type2 is ReferenceType.Ref -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    type1 is ReferenceType.RefNull && type2 is ReferenceType.RefNull -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    type1 is ReferenceType.Ref && type2 is ReferenceType.RefNull -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    else -> false
}
