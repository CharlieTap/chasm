package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.F32ConvertI64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32ConvertI64UDispatcher(
    instruction: NumericInstruction.F32ConvertI64U,
) = F32ConvertI64UDispatcher(
    instruction = instruction,
    executor = ::F32ConvertI64UExecutor,
)

internal inline fun F32ConvertI64UDispatcher(
    instruction: NumericInstruction.F32ConvertI64U,
    crossinline executor: Executor<NumericInstruction.F32ConvertI64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
