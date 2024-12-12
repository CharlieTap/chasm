package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BlockExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

fun BlockDispatcher(
    instruction: ControlInstruction.Block,
) = BlockDispatcher(
    instruction = instruction,
    executor = ::BlockExecutor,
)

internal inline fun BlockDispatcher(
    instruction: ControlInstruction.Block,
    crossinline executor: Executor<ControlInstruction.Block>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
