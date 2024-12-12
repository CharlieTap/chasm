package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32EqExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32EqDispatcher(
    instruction: NumericInstruction.F32Eq,
) = F32EqDispatcher(
    instruction = instruction,
    executor = ::F32EqExecutor,
)

internal inline fun F32EqDispatcher(
    instruction: NumericInstruction.F32Eq,
    crossinline executor: Executor<NumericInstruction.F32Eq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
