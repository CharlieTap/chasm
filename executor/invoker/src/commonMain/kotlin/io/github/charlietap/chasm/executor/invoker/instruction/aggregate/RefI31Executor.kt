package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.ext.wrapI31
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun RefI31Executor(
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
) =
    RefI31Executor(
        context = context,
        instruction = instruction,
        i31Wrapper = Int::wrapI31,
    )

internal inline fun RefI31Executor(
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
    crossinline i31Wrapper: (Int) -> ReferenceValue.I31,
) {
    val stack = context.vstack
    val value = stack.popI32()
    val i31 = i31Wrapper(value).toLong()

    stack.push(i31)
}
