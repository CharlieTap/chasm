package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.stack.Stack
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.type.LocalType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType

internal interface FunctionContext {
    val locals: MutableList<LocalType>
    val labels: Stack<Label>
    val result: ResultType?
    val operands: Stack<ValueType>
}

internal data class FunctionContextImpl(
    override val locals: MutableList<LocalType> = mutableListOf(),
    override val labels: Stack<Label> = stackOf(),
    override val result: ResultType? = null,
    override val operands: Stack<ValueType> = stackOf(),
) : FunctionContext
