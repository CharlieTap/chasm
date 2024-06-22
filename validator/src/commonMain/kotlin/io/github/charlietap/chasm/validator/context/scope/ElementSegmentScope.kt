package io.github.charlietap.chasm.validator.context.scope

import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.validator.context.ValidationContext

internal fun ElementSegmentScope(
    context: ValidationContext,
    segment: ElementSegment,
): ValidationContext = context.copy(
    module = context.module.copy(
        globals = emptyList(),
    ),
)
