package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.F64GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F64GeDispatcher(
    instruction: FusedNumericInstruction.F64Ge,
) = F64GeDispatcher(
    instruction = instruction,
    executor = ::F64GeExecutor,
)

internal inline fun F64GeDispatcher(
    instruction: FusedNumericInstruction.F64Ge,
    crossinline executor: Executor<FusedNumericInstruction.F64Ge>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
