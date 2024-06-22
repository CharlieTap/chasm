package io.github.charlietap.chasm.validator.context.scope

import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.validator.context.ValidationContext

internal fun DataSegmentScope(
    context: ValidationContext,
    segment: DataSegment,
): ValidationContext = context.copy(
    module = context.module.copy(
        globals = emptyList(),
    ),
)
