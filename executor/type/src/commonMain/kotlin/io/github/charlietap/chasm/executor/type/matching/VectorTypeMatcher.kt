package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.ast.type.VectorType

@Suppress("UNUSED_PARAMETER")
fun VectorTypeMatcher(
    type1: VectorType,
    type2: VectorType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
