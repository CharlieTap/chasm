package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun StructSetDispatcher(
    instruction: AggregateInstruction.StructSet,
) = StructSetDispatcher(
    instruction = instruction,
    executor = ::StructSetExecutor,
)

internal inline fun StructSetDispatcher(
    instruction: AggregateInstruction.StructSet,
    crossinline executor: Executor<AggregateInstruction.StructSet>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
