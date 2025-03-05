package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64NeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64NeDispatcher(
    instruction: NumericInstruction.I64Ne,
) = I64NeDispatcher(
    instruction = instruction,
    executor = ::I64NeExecutor,
)

internal inline fun I64NeDispatcher(
    instruction: NumericInstruction.I64Ne,
    crossinline executor: Executor<NumericInstruction.I64Ne>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
