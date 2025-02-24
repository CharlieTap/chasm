package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.type.VectorType

@Suppress("UNUSED_PARAMETER")
internal fun VectorTypeMatcher(
    type1: VectorType,
    type2: VectorType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
