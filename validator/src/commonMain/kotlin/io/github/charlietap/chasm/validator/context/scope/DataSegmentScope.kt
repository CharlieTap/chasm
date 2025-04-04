package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun DataSegmentScope(
    context: ValidationContext,
    segment: DataSegment,
): Result<ValidationContext, ModuleValidatorError> = context
    .copy(
        expressionContext = ExpressionContextImpl(
            expressionResultType = ResultType(listOf(ValueType.Number(NumberType.I32))),
        ),
    ).let(::Ok)
