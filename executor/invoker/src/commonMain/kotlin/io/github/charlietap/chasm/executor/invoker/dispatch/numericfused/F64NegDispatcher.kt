package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop.F64NegExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64NegDispatcher(
    instruction: FusedNumericInstruction.F64Neg,
) = F64NegDispatcher(
    instruction = instruction,
    executor = ::F64NegExecutor,
)

internal inline fun F64NegDispatcher(
    instruction: FusedNumericInstruction.F64Neg,
    crossinline executor: Executor<FusedNumericInstruction.F64Neg>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
