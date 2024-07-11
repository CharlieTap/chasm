package io.github.charlietap.chasm.validator.validator.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.ElementSegmentScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator

internal fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
): Result<Unit, ModuleValidatorError> =
    ElementSegmentValidator(
        context = context,
        segment = segment,
        scope = ::ElementSegmentScope,
        segmentModeValidator = ::ElementSegmentModeValidator,
        expressionValidator = ::ExpressionValidator,
    )

internal fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
    scope: Scope<ElementSegment>,
    segmentModeValidator: Validator<ElementSegment.Mode>,
    expressionValidator: Validator<Expression>,
): Result<Unit, ModuleValidatorError> = binding {

    val scopedContext = scope(context, segment).bind()

    segmentModeValidator(scopedContext, segment.mode).bind()

    segment.initExpressions.forEach { expression ->
        expressionValidator(scopedContext, expression).bind()
    }
}
