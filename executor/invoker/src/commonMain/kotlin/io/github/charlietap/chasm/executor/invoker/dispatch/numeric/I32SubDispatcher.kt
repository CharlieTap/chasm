package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32SubExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32SubDispatcher(
    instruction: NumericInstruction.I32Sub,
) = I32SubDispatcher(
    instruction = instruction,
    executor = ::I32SubExecutor,
)

internal inline fun I32SubDispatcher(
    instruction: NumericInstruction.I32Sub,
    crossinline executor: Executor<NumericInstruction.I32Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
