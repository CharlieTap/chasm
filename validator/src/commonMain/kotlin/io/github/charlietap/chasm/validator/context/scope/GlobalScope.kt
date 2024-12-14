package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.validator.context.ExpressionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun GlobalScope(
    context: ValidationContext,
    global: Global,
): Result<ValidationContext, ModuleValidatorError> = context
    .copy(
        module = context.module.copy(
            globals = context.module.globals.filter { glob ->
                glob.idx.idx < global.idx.idx
            },
        ),
        expressionContext = ExpressionContextImpl(
            expressionResultType = ResultType(listOf(global.type.valueType)),
        ),
    ).let(::Ok)
