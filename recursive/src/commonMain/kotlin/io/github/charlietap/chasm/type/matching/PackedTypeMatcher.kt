package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ast.type.PackedType

@Suppress("UNUSED_PARAMETER")
internal fun PackedTypeMatcher(
    type1: PackedType,
    type2: PackedType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
