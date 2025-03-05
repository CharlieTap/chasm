package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F64NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F64NegDispatcher(
    instruction: NumericInstruction.F64Neg,
) = F64NegDispatcher(
    instruction = instruction,
    executor = ::F64NegExecutor,
)

internal inline fun F64NegDispatcher(
    instruction: NumericInstruction.F64Neg,
    crossinline executor: Executor<NumericInstruction.F64Neg>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
