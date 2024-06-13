package io.github.charlietap.chasm.validator.validator.data

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.DataSegmentScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun DataSegmentValidator(
    context: ValidationContext,
    segment: DataSegment,
): Result<Unit, ModuleValidatorError> =
    DataSegmentValidator(
        context = context,
        segment = segment,
        scope = ::DataSegmentScope,
        segmentModeValidator = ::DataSegmentModeValidator,
    )

internal fun DataSegmentValidator(
    context: ValidationContext,
    segment: DataSegment,
    scope: Scope<DataSegment>,
    segmentModeValidator: Validator<DataSegment.Mode>,
): Result<Unit, ModuleValidatorError> = binding {
    val scopedContext = scope(context, segment).bind()
    segmentModeValidator(scopedContext, segment.mode).bind()
}
