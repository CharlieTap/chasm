package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType

internal interface FunctionContext {
    val locals: MutableList<ValueType>
    val labels: MutableList<ResultType>
}

internal data class FunctionContextImpl(
    override val locals: MutableList<ValueType> = mutableListOf(),
    override val labels: MutableList<ResultType> = mutableListOf(),
) : FunctionContext
