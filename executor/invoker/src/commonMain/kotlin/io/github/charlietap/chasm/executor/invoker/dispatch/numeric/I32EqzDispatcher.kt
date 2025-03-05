package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I32EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32EqzDispatcher(
    instruction: NumericInstruction.I32Eqz,
) = I32EqzDispatcher(
    instruction = instruction,
    executor = ::I32EqzExecutor,
)

internal inline fun I32EqzDispatcher(
    instruction: NumericInstruction.I32Eqz,
    crossinline executor: Executor<NumericInstruction.I32Eqz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
