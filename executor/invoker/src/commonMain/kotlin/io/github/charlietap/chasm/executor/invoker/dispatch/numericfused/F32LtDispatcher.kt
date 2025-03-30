package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F32LtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32LtDispatcher(
    instruction: FusedNumericInstruction.F32Lt,
) = F32LtDispatcher(
    instruction = instruction,
    executor = ::F32LtExecutor,
)

internal inline fun F32LtDispatcher(
    instruction: FusedNumericInstruction.F32Lt,
    crossinline executor: Executor<FusedNumericInstruction.F32Lt>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
