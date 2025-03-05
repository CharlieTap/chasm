package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtU,
) = I32GtUDispatcher(
    instruction = instruction,
    executor = ::I32GtUExecutor,
)

internal inline fun I32GtUDispatcher(
    instruction: NumericInstruction.I32GtU,
    crossinline executor: Executor<NumericInstruction.I32GtU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
