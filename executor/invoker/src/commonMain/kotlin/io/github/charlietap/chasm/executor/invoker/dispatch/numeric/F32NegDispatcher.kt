package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NegExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32NegDispatcher(
    instruction: NumericInstruction.F32Neg,
) = F32NegDispatcher(
    instruction = instruction,
    executor = ::F32NegExecutor,
)

internal inline fun F32NegDispatcher(
    instruction: NumericInstruction.F32Neg,
    crossinline executor: Executor<NumericInstruction.F32Neg>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
