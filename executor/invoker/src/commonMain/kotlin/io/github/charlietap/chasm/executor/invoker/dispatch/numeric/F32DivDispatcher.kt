package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32DivExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32DivDispatcher(
    instruction: NumericInstruction.F32Div,
) = F32DivDispatcher(
    instruction = instruction,
    executor = ::F32DivExecutor,
)

internal inline fun F32DivDispatcher(
    instruction: NumericInstruction.F32Div,
    crossinline executor: Executor<NumericInstruction.F32Div>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
