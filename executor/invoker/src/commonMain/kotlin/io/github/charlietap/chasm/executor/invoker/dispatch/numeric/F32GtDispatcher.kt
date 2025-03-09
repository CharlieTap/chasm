package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.F32GtExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32GtDispatcher(
    instruction: NumericInstruction.F32Gt,
) = F32GtDispatcher(
    instruction = instruction,
    executor = ::F32GtExecutor,
)

internal inline fun F32GtDispatcher(
    instruction: NumericInstruction.F32Gt,
    crossinline executor: Executor<NumericInstruction.F32Gt>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
