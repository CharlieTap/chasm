package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ElementSegmentContextImpl
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ElementSegmentScope(
    context: ValidationContext,
    segment: ElementSegment,
): Result<ValidationContext, ModuleValidatorError> = context.copy(
    module = context.module.copy(
        globals = emptyList(),
    ),
    elementSegmentContext = ElementSegmentContextImpl(
        elementSegmentType = segment.type,
    ),
    expressionContext = ExpressionContextImpl(
        expressionResultType = ResultType(listOf(ValueType.Reference(segment.type))),
    ),
).let(::Ok)
