package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32PopcntExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32PopcntDispatcher(
    instruction: NumericInstruction.I32Popcnt,
) = I32PopcntDispatcher(
    instruction = instruction,
    executor = ::I32PopcntExecutor,
)

internal inline fun I32PopcntDispatcher(
    instruction: NumericInstruction.I32Popcnt,
    crossinline executor: Executor<NumericInstruction.I32Popcnt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
