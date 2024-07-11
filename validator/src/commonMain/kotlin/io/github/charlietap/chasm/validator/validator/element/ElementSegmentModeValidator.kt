package io.github.charlietap.chasm.validator.validator.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.ElementSegmentModeScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.elementSegmentType
import io.github.charlietap.chasm.validator.ext.tableType
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator

internal fun ElementSegmentModeValidator(
    context: ValidationContext,
    mode: ElementSegment.Mode,
): Result<Unit, ModuleValidatorError> =
    ElementSegmentModeValidator(
        context = context,
        mode = mode,
        scope = ::ElementSegmentModeScope,
        expressionValidator = ::ExpressionValidator,
        typeMatcher = ::ReferenceTypeMatcher,
    )

internal fun ElementSegmentModeValidator(
    context: ValidationContext,
    mode: ElementSegment.Mode,
    scope: Scope<ElementSegment.Mode>,
    expressionValidator: Validator<Expression>,
    typeMatcher: TypeMatcher<ReferenceType>,
): Result<Unit, ModuleValidatorError> = binding {

    val scopedContext = scope(context, mode).bind()

    when (mode) {
        is ElementSegment.Mode.Active -> {

            val tableType = context.tableType(mode.tableIndex).bind()
            val segmentType = context.elementSegmentType().bind()

            if (!typeMatcher(tableType.referenceType, segmentType, context)) {
                Err(TypeValidatorError.TypeMismatch).bind<Unit>()
            }

            expressionValidator(scopedContext, mode.offsetExpr).bind()
        }
        ElementSegment.Mode.Declarative -> Unit
        ElementSegment.Mode.Passive -> Unit
    }
}
