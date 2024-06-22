package io.github.charlietap.chasm.validator.context.scope

import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.validator.context.GlobalContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext

internal fun GlobalScope(
    context: ValidationContext,
    global: Global,
): ValidationContext = context.copy(
    globalContext = GlobalContextImpl(
        expressionsMustBeConstant = true,
    ),
)
