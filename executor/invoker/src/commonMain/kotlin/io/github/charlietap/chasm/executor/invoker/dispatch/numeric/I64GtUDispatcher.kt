package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GtUExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtU,
) = I64GtUDispatcher(
    instruction = instruction,
    executor = ::I64GtUExecutor,
)

internal inline fun I64GtUDispatcher(
    instruction: NumericInstruction.I64GtU,
    crossinline executor: Executor<NumericInstruction.I64GtU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
