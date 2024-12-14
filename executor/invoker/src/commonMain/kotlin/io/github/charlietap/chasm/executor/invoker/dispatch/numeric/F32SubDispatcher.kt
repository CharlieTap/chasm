package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32SubExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F32SubDispatcher(
    instruction: NumericInstruction.F32Sub,
) = F32SubDispatcher(
    instruction = instruction,
    executor = ::F32SubExecutor,
)

internal inline fun F32SubDispatcher(
    instruction: NumericInstruction.F32Sub,
    crossinline executor: Executor<NumericInstruction.F32Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
