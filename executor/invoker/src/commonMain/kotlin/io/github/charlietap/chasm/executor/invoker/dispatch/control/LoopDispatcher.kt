package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.LoopExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun LoopDispatcher(
    instruction: ControlInstruction.Loop,
) = LoopDispatcher(
    instruction = instruction,
    executor = ::LoopExecutor,
)

internal inline fun LoopDispatcher(
    instruction: ControlInstruction.Loop,
    crossinline executor: Executor<ControlInstruction.Loop>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
