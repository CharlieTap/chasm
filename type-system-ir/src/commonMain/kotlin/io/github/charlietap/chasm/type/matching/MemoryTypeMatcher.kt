package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.Limits
import io.github.charlietap.chasm.ir.type.MemoryType

fun MemoryTypeMatcher(
    type1: MemoryType,
    type2: MemoryType,
    context: TypeMatcherContext,
): Boolean = MemoryTypeMatcher(
    type1 = type1,
    type2 = type2,
    context = context,
    limitsMatcher = ::LimitsMatcher,
)

internal fun MemoryTypeMatcher(
    type1: MemoryType,
    type2: MemoryType,
    context: TypeMatcherContext,
    limitsMatcher: TypeMatcher<Limits>,
): Boolean = limitsMatcher(type1.limits, type2.limits, context)
