package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI64SDispatcher(
    instruction: NumericInstruction.F32ConvertI64S,
) = F32ConvertI64SDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI64SExecutor,
)

internal inline fun F32ConvertI64SDispatcher(
    instruction: NumericInstruction.F32ConvertI64S,
    crossinline executor: Executor<NumericInstruction.F32ConvertI64S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
