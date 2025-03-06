package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.I32PopcntExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32PopcntDispatcher(
    instruction: FusedNumericInstruction.I32Popcnt,
) = I32PopcntDispatcher(
    instruction = instruction,
    executor = ::I32PopcntExecutor,
)

internal inline fun I32PopcntDispatcher(
    instruction: FusedNumericInstruction.I32Popcnt,
    crossinline executor: Executor<FusedNumericInstruction.I32Popcnt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
