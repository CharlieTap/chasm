package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.type.ResultType

internal interface ExpressionContext {
    val expressionResultType: ResultType?
}

internal data class ExpressionContextImpl(
    override val expressionResultType: ResultType? = null,
) : ExpressionContext
