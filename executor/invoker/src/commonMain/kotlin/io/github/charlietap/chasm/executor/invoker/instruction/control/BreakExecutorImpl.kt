@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.flow.BreakException
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekNthLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue

internal inline fun BreakExecutorImpl(
    stack: Stack,
    labelIndex: Index.LabelIndex,
): Result<Unit, InvocationError> = binding {

    val label = stack.peekNthLabel(labelIndex.index()).bind()

    val results = List(label.arity.value) {
        stack.popValue().bind().value
    }.asReversed()

    throw BreakException(labelIndex.idx.toInt(), results, label.continuation)
}
