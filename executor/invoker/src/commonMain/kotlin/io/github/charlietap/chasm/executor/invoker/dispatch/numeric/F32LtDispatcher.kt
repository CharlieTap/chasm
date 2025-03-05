package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32LtDispatcher(
    instruction: NumericInstruction.F32Lt,
) = F32LtDispatcher(
    instruction = instruction,
    executor = ::F32LtExecutor,
)

internal inline fun F32LtDispatcher(
    instruction: NumericInstruction.F32Lt,
    crossinline executor: Executor<NumericInstruction.F32Lt>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
