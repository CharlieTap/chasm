package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ir.type.Limits as IRLimits

internal inline fun LimitsFactory(
    limits: Limits,
): IRLimits {
    return IRLimits(
        min = limits.min,
        max = limits.max,
    )
}
