package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.NumberType

@Suppress("UNUSED_PARAMETER")
internal fun NumberTypeMatcher(
    type1: NumberType,
    type2: NumberType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
