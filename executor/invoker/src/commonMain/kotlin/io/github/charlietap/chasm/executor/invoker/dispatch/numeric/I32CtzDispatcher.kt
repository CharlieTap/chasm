package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32CtzExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32CtzDispatcher(
    instruction: NumericInstruction.I32Ctz,
) = I32CtzDispatcher(
    instruction = instruction,
    executor = ::I32CtzExecutor,
)

internal inline fun I32CtzDispatcher(
    instruction: NumericInstruction.I32Ctz,
    crossinline executor: Executor<NumericInstruction.I32Ctz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
