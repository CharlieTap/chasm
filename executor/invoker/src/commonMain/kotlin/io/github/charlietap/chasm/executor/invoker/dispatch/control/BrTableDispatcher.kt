package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.BrTableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun BrTableDispatcher(
    instruction: ControlInstruction.BrTable,
) = BrTableDispatcher(
    instruction = instruction,
    executor = ::BrTableExecutor,
)

internal inline fun BrTableDispatcher(
    instruction: ControlInstruction.BrTable,
    crossinline executor: Executor<ControlInstruction.BrTable>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
