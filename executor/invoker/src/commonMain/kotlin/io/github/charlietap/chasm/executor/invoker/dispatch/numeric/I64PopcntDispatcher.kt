package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64PopcntExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64Popcnt,
) = I64PopcntDispatcher(
    instruction = instruction,
    executor = ::I64PopcntExecutor,
)

internal inline fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64Popcnt,
    crossinline executor: Executor<NumericInstruction.I64Popcnt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
