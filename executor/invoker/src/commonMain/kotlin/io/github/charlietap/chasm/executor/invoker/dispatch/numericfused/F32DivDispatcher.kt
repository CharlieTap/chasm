package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.F32DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun F32DivDispatcher(
    instruction: FusedNumericInstruction.F32Div,
) = F32DivDispatcher(
    instruction = instruction,
    executor = ::F32DivExecutor,
)

internal inline fun F32DivDispatcher(
    instruction: FusedNumericInstruction.F32Div,
    crossinline executor: Executor<FusedNumericInstruction.F32Div>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
