package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction

internal fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
) =
    StructNewDefaultExecutor(
        context = context,
        instruction = instruction,
        structNewExecutor = ::StructNewExecutor,
    )

internal inline fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
    crossinline structNewExecutor: Executor<AggregateInstruction.StructNew>,
) {
    val stack = context.vstack

    stack.push(instruction.fields)

    structNewExecutor(
        context,
        AggregateInstruction.StructNew(
            definedType = instruction.definedType,
            structType = instruction.structType,
        ),
    )
}
