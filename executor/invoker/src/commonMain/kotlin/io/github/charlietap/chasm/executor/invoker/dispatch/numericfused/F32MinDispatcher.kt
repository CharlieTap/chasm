package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32MinExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32MinDispatcher(
    instruction: FusedNumericInstruction.F32Min,
) = F32MinDispatcher(
    instruction = instruction,
    executor = ::F32MinExecutor,
)

internal inline fun F32MinDispatcher(
    instruction: FusedNumericInstruction.F32Min,
    crossinline executor: Executor<FusedNumericInstruction.F32Min>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
