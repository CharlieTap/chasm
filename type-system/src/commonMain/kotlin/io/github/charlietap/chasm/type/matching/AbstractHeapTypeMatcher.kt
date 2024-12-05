package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.AbstractHeapType

@Suppress("UNUSED_PARAMETER")
internal fun AbstractHeapTypeMatcher(
    type1: AbstractHeapType,
    type2: AbstractHeapType,
    context: TypeMatcherContext,
): Boolean = when(type1) {
    is AbstractHeapType.Eq if type2 is AbstractHeapType.Any -> true
    is AbstractHeapType.Struct if type2 is AbstractHeapType.Any -> true
    is AbstractHeapType.Array if type2 is AbstractHeapType.Any -> true
    is AbstractHeapType.I31 if type2 is AbstractHeapType.Any -> true
    is AbstractHeapType.I31 if type2 is AbstractHeapType.Eq -> true
    is AbstractHeapType.Struct if type2 is AbstractHeapType.Eq -> true
    is AbstractHeapType.Array if type2 is AbstractHeapType.Eq -> true
    is AbstractHeapType.Extern if type2 is AbstractHeapType.Extern -> true
    else -> type1 == type2
}
