package io.github.charlietap.chasm.validator.validator.element

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.ElementSegmentScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator
import io.github.charlietap.chasm.validator.validator.type.ReferenceTypeValidator

internal fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
): Result<Unit, ModuleValidatorError> =
    ElementSegmentValidator(
        context = context,
        segment = segment,
        scope = ::ElementSegmentScope,
        referenceTypeValidator = ::ReferenceTypeValidator,
        segmentModeValidator = ::ElementSegmentModeValidator,
        expressionValidator = ::ExpressionValidator,
    )

internal inline fun ElementSegmentValidator(
    context: ValidationContext,
    segment: ElementSegment,
    crossinline scope: Scope<ElementSegment>,
    crossinline referenceTypeValidator: Validator<ReferenceType>,
    crossinline segmentModeValidator: Validator<ElementSegment.Mode>,
    crossinline expressionValidator: Validator<Expression>,
): Result<Unit, ModuleValidatorError> = binding {

    val scopedContext = scope(context, segment).bind()

    referenceTypeValidator(scopedContext, segment.type).bind()
    segmentModeValidator(scopedContext, segment.mode).bind()

    segment.initExpressions.forEach { expression ->
        expressionValidator(scopedContext, expression).bind()
    }
}
