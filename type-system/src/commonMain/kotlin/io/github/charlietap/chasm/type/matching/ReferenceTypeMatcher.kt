package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType

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
): Boolean = when(type1) {
    is ReferenceType.Ref if type2 is ReferenceType.Ref -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    is ReferenceType.RefNull if type2 is ReferenceType.RefNull -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    is ReferenceType.Ref if type2 is ReferenceType.RefNull -> {
        heapTypeMatcher(type1.heapType, type2.heapType, context)
    }
    else -> false
}
