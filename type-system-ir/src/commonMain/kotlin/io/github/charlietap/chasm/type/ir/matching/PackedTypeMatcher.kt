package io.github.charlietap.chasm.type.ir.matching

import io.github.charlietap.chasm.ir.type.PackedType

@Suppress("UNUSED_PARAMETER")
internal fun PackedTypeMatcher(
    type1: PackedType,
    type2: PackedType,
    context: TypeMatcherContext,
): Boolean = type1 == type2
