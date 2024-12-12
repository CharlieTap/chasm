package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32EqExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32EqDispatcher(
    instruction: NumericInstruction.I32Eq,
) = I32EqDispatcher(
    instruction = instruction,
    executor = ::I32EqExecutor,
)

internal inline fun I32EqDispatcher(
    instruction: NumericInstruction.I32Eq,
    crossinline executor: Executor<NumericInstruction.I32Eq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
