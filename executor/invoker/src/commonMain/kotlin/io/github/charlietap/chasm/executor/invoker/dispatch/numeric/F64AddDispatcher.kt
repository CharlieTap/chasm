package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64AddExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64AddDispatcher(
    instruction: NumericInstruction.F64Add,
) = F64AddDispatcher(
    instruction = instruction,
    executor = ::F64AddExecutor,
)

internal inline fun F64AddDispatcher(
    instruction: NumericInstruction.F64Add,
    crossinline executor: Executor<NumericInstruction.F64Add>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
