package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32LeExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32LeDispatcher(
    instruction: NumericInstruction.F32Le,
) = F32LeDispatcher(
    instruction = instruction,
    executor = ::F32LeExecutor,
)

internal inline fun F32LeDispatcher(
    instruction: NumericInstruction.F32Le,
    crossinline executor: Executor<NumericInstruction.F32Le>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
