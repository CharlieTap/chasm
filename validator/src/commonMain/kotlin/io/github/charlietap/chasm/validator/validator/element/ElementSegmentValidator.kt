package io.github.charlietap.chasm.validator.validator.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
): Result<Unit, ModuleValidatorError> =
    ElementSegmentValidator(
        context = context,
        segment = segment,
        segmentModeValidator = ::ElementSegmentModeValidator,
    )

internal fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
    segmentModeValidator: Validator<ElementSegment.Mode>,
): Result<Unit, ModuleValidatorError> = binding {
    segmentModeValidator(context, segment.mode).bind()
}
