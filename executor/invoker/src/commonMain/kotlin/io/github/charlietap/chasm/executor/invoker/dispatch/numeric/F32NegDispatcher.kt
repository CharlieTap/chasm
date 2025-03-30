package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32NegDispatcher(
    instruction: NumericInstruction.F32Neg,
) = F32NegDispatcher(
    instruction = instruction,
    executor = ::F32NegExecutor,
)

internal inline fun F32NegDispatcher(
    instruction: NumericInstruction.F32Neg,
    crossinline executor: Executor<NumericInstruction.F32Neg>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
