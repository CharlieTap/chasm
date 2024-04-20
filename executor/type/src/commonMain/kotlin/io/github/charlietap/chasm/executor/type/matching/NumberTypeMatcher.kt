package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.ast.type.NumberType

@Suppress("UNUSED_PARAMETER")
fun NumberTypeMatcher(
    type1: NumberType,
    type2: NumberType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
