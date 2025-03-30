package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32EqDispatcher(
    instruction: FusedNumericInstruction.F32Eq,
) = F32EqDispatcher(
    instruction = instruction,
    executor = ::F32EqExecutor,
)

internal inline fun F32EqDispatcher(
    instruction: FusedNumericInstruction.F32Eq,
    crossinline executor: Executor<FusedNumericInstruction.F32Eq>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
