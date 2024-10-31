package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun TableScope(
    context: ValidationContext,
    table: Table,
): Result<ValidationContext, ModuleValidatorError> = context.copy(
    module = context.module.copy(
        globals = emptyList(),
    ),
    expressionContext = ExpressionContextImpl(
        expressionResultType = ResultType(
            listOf(ValueType.Reference(table.type.referenceType)),
        ),
    ),
).let(::Ok)
