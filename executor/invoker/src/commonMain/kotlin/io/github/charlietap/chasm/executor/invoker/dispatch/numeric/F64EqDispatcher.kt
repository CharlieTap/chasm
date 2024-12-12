package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F64EqExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64EqDispatcher(
    instruction: NumericInstruction.F64Eq,
) = F64EqDispatcher(
    instruction = instruction,
    executor = ::F64EqExecutor,
)

internal inline fun F64EqDispatcher(
    instruction: NumericInstruction.F64Eq,
    crossinline executor: Executor<NumericInstruction.F64Eq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
