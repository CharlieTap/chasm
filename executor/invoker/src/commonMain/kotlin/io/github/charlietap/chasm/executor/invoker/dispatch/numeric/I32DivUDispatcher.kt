package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32DivUExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivU,
) = I32DivUDispatcher(
    instruction = instruction,
    executor = ::I32DivUExecutor,
)

internal inline fun I32DivUDispatcher(
    instruction: NumericInstruction.I32DivU,
    crossinline executor: Executor<NumericInstruction.I32DivU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
