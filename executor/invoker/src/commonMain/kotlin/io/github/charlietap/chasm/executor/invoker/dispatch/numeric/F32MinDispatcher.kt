package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MinExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32MinDispatcher(
    instruction: NumericInstruction.F32Min,
) = F32MinDispatcher(
    instruction = instruction,
    executor = ::F32MinExecutor,
)

internal inline fun F32MinDispatcher(
    instruction: NumericInstruction.F32Min,
    crossinline executor: Executor<NumericInstruction.F32Min>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}