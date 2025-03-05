package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.testop.I64EqzExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqzDispatcher(
    instruction: NumericInstruction.I64Eqz,
) = I64EqzDispatcher(
    instruction = instruction,
    executor = ::I64EqzExecutor,
)

internal inline fun I64EqzDispatcher(
    instruction: NumericInstruction.I64Eqz,
    crossinline executor: Executor<NumericInstruction.I64Eqz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
