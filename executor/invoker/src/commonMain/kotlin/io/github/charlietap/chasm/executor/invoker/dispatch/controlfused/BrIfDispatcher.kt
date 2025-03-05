package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrIfExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction

fun BrIfDispatcher(
    instruction: FusedControlInstruction.BrIf,
) = BrIfDispatcher(
    instruction = instruction,
    executor = ::BrIfExecutor,
)

internal inline fun BrIfDispatcher(
    instruction: FusedControlInstruction.BrIf,
    crossinline executor: Executor<FusedControlInstruction.BrIf>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
