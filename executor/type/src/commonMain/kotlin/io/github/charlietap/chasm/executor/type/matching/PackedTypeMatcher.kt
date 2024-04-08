package io.github.charlietap.chasm.executor.type.matching

import io.github.charlietap.chasm.ast.type.PackedType

@Suppress("UNUSED_PARAMETER")
fun PackedTypeMatcher(
    type1: PackedType,
    type2: PackedType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
