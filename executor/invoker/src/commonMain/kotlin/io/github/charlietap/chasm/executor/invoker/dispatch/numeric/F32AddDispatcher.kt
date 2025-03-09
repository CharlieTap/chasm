package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32AddDispatcher(
    instruction: NumericInstruction.F32Add,
) = F32AddDispatcher(
    instruction = instruction,
    executor = ::F32AddExecutor,
)

internal inline fun F32AddDispatcher(
    instruction: NumericInstruction.F32Add,
    crossinline executor: Executor<NumericInstruction.F32Add>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
