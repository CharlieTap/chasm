@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.executor.invoker.ext.popValueOrError
import io.github.charlietap.chasm.executor.invoker.flow.BreakException
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError

internal inline fun BreakExecutorImpl(
    stack: Stack,
    labelIndex: Index.LabelIndex,
): Result<Unit, InvocationError> = binding {
    stack.peekNthLabel(labelIndex.idx.toInt())?.let { label ->

        val results = List(label.arity.value) {
            stack.popValueOrError().bind().value
        }

        throw BreakException(labelIndex.idx.toInt(), results, label.continuation)
    } ?: Err(InvocationError.MissingStackLabel).bind<Unit>()
}
