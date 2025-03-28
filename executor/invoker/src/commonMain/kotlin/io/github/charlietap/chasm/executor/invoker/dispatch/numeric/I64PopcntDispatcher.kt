package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64PopcntExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64Popcnt,
) = I64PopcntDispatcher(
    instruction = instruction,
    executor = ::I64PopcntExecutor,
)

internal inline fun I64PopcntDispatcher(
    instruction: NumericInstruction.I64Popcnt,
    crossinline executor: Executor<NumericInstruction.I64Popcnt>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
