package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GeExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32GeDispatcher(
    instruction: NumericInstruction.F32Ge,
) = F32GeDispatcher(
    instruction = instruction,
    executor = ::F32GeExecutor,
)

internal inline fun F32GeDispatcher(
    instruction: NumericInstruction.F32Ge,
    crossinline executor: Executor<NumericInstruction.F32Ge>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
