package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.F32CeilExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32CeilDispatcher(
    instruction: NumericInstruction.F32Ceil,
) = F32CeilDispatcher(
    instruction = instruction,
    executor = ::F32CeilExecutor,
)

internal inline fun F32CeilDispatcher(
    instruction: NumericInstruction.F32Ceil,
    crossinline executor: Executor<NumericInstruction.F32Ceil>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
