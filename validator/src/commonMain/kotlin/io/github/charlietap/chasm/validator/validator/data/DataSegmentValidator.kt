package io.github.charlietap.chasm.validator.validator.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun DataSegmentValidator(
    context: ValidationContext,
    segment: DataSegment,
): Result<Unit, ModuleValidatorError> =
    DataSegmentValidator(
        context = context,
        segment = segment,
        segmentModeValidator = ::DataSegmentModeValidator,
    )

internal inline fun DataSegmentValidator(
    context: ValidationContext,
    segment: DataSegment,
    crossinline segmentModeValidator: Validator<DataSegment.Mode>,
): Result<Unit, ModuleValidatorError> = binding {
    segmentModeValidator(context, segment.mode).bind()
}
