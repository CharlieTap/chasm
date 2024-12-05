package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.wrapI31
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun RefI31Executor(
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
): Result<Unit, InvocationError> =
    RefI31Executor(
        context = context,
        instruction = instruction,
        i31Wrapper = Int::wrapI31,
    )

internal inline fun RefI31Executor(
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
    crossinline i31Wrapper: (Int) -> ReferenceValue.I31,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val value = stack.popI32().bind()

    val i31 = i31Wrapper(value)

    stack.pushValue(i31)
}
