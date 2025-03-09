package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32EqDispatcher(
    instruction: NumericInstruction.F32Eq,
) = F32EqDispatcher(
    instruction = instruction,
    executor = ::F32EqExecutor,
)

internal inline fun F32EqDispatcher(
    instruction: NumericInstruction.F32Eq,
    crossinline executor: Executor<NumericInstruction.F32Eq>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
