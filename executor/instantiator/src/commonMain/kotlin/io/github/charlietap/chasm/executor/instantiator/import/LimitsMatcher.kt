package io.github.charlietap.chasm.executor.instantiator.import

import io.github.charlietap.chasm.ast.type.Limits

internal typealias LimitsMatcher = (Limits, Limits) -> Boolean

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
