package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32NeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32Ne,
) = I32NeDispatcher(
    instruction = instruction,
    executor = ::I32NeExecutor,
)

internal inline fun I32NeDispatcher(
    instruction: FusedNumericInstruction.I32Ne,
    crossinline executor: Executor<FusedNumericInstruction.I32Ne>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
