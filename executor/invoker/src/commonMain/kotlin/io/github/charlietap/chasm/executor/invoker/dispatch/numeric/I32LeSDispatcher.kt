package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeS,
) = I32LeSDispatcher(
    instruction = instruction,
    executor = ::I32LeSExecutor,
)

internal inline fun I32LeSDispatcher(
    instruction: NumericInstruction.I32LeS,
    crossinline executor: Executor<NumericInstruction.I32LeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
