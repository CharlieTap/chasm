package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F32NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32NegDispatcher(
    instruction: FusedNumericInstruction.F32Neg,
) = F32NegDispatcher(
    instruction = instruction,
    executor = ::F32NegExecutor,
)

internal inline fun F32NegDispatcher(
    instruction: FusedNumericInstruction.F32Neg,
    crossinline executor: Executor<FusedNumericInstruction.F32Neg>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
