package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.AbstractHeapType

@Suppress("UNUSED_PARAMETER")
internal fun AbstractHeapTypeMatcher(
    type1: AbstractHeapType,
    type2: AbstractHeapType,
    context: TypeMatcherContext,
): Boolean = when {
    type1 is AbstractHeapType.Eq && type2 is AbstractHeapType.Any -> true
    type1 is AbstractHeapType.Struct && type2 is AbstractHeapType.Any -> true
    type1 is AbstractHeapType.Array && type2 is AbstractHeapType.Any -> true
    type1 is AbstractHeapType.I31 && type2 is AbstractHeapType.Any -> true
    type1 is AbstractHeapType.I31 && type2 is AbstractHeapType.Eq -> true
    type1 is AbstractHeapType.Struct && type2 is AbstractHeapType.Eq -> true
    type1 is AbstractHeapType.Array && type2 is AbstractHeapType.Eq -> true
    type1 is AbstractHeapType.Extern && type2 is AbstractHeapType.Extern -> true
    else -> type1 == type2
}
