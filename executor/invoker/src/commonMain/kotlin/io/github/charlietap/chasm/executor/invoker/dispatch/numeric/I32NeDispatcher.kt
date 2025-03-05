package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32NeDispatcher(
    instruction: NumericInstruction.I32Ne,
) = I32NeDispatcher(
    instruction = instruction,
    executor = ::I32NeExecutor,
)

internal inline fun I32NeDispatcher(
    instruction: NumericInstruction.I32Ne,
    crossinline executor: Executor<NumericInstruction.I32Ne>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
