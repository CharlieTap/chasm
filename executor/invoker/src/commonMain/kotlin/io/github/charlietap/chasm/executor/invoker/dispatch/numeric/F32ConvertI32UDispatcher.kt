package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI32UDispatcher(
    instruction: NumericInstruction.F32ConvertI32U,
) = F32ConvertI32UDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI32UExecutor,
)

internal inline fun F32ConvertI32UDispatcher(
    instruction: NumericInstruction.F32ConvertI32U,
    crossinline executor: Executor<NumericInstruction.F32ConvertI32U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
