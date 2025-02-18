package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.IfExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedControlInstruction

fun IfDispatcher(
    instruction: FusedControlInstruction.If,
) = IfDispatcher(
    instruction = instruction,
    executor = ::IfExecutor,
)

internal inline fun IfDispatcher(
    instruction: FusedControlInstruction.If,
    crossinline executor: Executor<FusedControlInstruction.If>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
