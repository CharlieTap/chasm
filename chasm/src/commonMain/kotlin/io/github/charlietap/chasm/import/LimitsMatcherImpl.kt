package io.github.charlietap.chasm.import

import io.github.charlietap.chasm.ast.type.Limits

internal fun LimitsMatcherImpl(
    first: Limits,
    second: Limits,
): Boolean {
    return if (first.min >= second.min) {
        if (second.max == null) {
            true
        } else {
            first.max != null && first.max!! <= second.max!!
        }
    } else {
        false
    }
}
