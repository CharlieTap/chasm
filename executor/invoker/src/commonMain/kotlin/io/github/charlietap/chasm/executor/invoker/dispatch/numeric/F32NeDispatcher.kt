package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32NeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32NeDispatcher(
    instruction: NumericInstruction.F32Ne,
) = F32NeDispatcher(
    instruction = instruction,
    executor = ::F32NeExecutor,
)

internal inline fun F32NeDispatcher(
    instruction: NumericInstruction.F32Ne,
    crossinline executor: Executor<NumericInstruction.F32Ne>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
