package io.github.charlietap.chasm.type.matching

import io.github.charlietap.chasm.ir.type.Limits

fun LimitsMatcher(
    type1: Limits,
    type2: Limits,
    context: TypeMatcherContext,
): Boolean = if (type1.min >= type2.min) {
    if (type2.max == null) {
        true
    } else {
        type1.max != null && type1.max!! <= type2.max!!
    }
} else {
    false
}
