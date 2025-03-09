package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.IfExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction

fun IfDispatcher(
    instruction: FusedControlInstruction.If,
) = IfDispatcher(
    instruction = instruction,
    executor = ::IfExecutor,
)

internal inline fun IfDispatcher(
    instruction: FusedControlInstruction.If,
    crossinline executor: Executor<FusedControlInstruction.If>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
