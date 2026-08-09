package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.stack.Stack
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.type.LocalType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType

internal interface FunctionContext {
    var locals: MutableList<LocalType>
    val localChanges: MutableList<Int>
    var labels: Stack<Label>
    var result: ResultType?
    var operands: Stack<ValueType>
}

internal data class FunctionContextImpl(
    override var locals: MutableList<LocalType> = [],
    override val localChanges: MutableList<Int> = [],
    override var labels: Stack<Label> = stackOf(),
    override var result: ResultType? = null,
    override var operands: Stack<ValueType> = stackOf(),
) : FunctionContext
