package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtU,
) = I32LtUDispatcher(
    instruction = instruction,
    executor = ::I32LtUExecutor,
)

internal inline fun I32LtUDispatcher(
    instruction: NumericInstruction.I32LtU,
    crossinline executor: Executor<NumericInstruction.I32LtU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
