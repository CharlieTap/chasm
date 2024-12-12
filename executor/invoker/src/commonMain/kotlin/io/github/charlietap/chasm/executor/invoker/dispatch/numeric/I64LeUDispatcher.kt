package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeUExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeU,
) = I64LeUDispatcher(
    instruction = instruction,
    executor = ::I64LeUExecutor,
)

internal inline fun I64LeUDispatcher(
    instruction: NumericInstruction.I64LeU,
    crossinline executor: Executor<NumericInstruction.I64LeU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
