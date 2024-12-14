package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32ClzExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32ClzDispatcher(
    instruction: NumericInstruction.I32Clz,
) = I32ClzDispatcher(
    instruction = instruction,
    executor = ::I32ClzExecutor,
)

internal inline fun I32ClzDispatcher(
    instruction: NumericInstruction.I32Clz,
    crossinline executor: Executor<NumericInstruction.I32Clz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}