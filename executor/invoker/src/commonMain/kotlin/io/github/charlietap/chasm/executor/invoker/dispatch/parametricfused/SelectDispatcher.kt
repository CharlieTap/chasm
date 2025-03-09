package io.github.charlietap.chasm.executor.invoker.dispatch.parametricfused

import io.github.charlietap.chasm.executor.invoker.instruction.parametricfused.SelectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedParametricInstruction

fun SelectDispatcher(
    instruction: FusedParametricInstruction.Select,
) = SelectDispatcher(
    instruction = instruction,
    executor = ::SelectExecutor,
)

internal inline fun SelectDispatcher(
    instruction: FusedParametricInstruction.Select,
    crossinline executor: Executor<FusedParametricInstruction.Select>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
