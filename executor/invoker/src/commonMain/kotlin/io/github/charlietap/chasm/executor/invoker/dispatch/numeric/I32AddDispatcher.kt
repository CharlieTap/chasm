package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AddExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32AddDispatcher(
    instruction: NumericInstruction.I32Add,
) = I32AddDispatcher(
    instruction = instruction,
    executor = ::I32AddExecutor,
)

internal inline fun I32AddDispatcher(
    instruction: NumericInstruction.I32Add,
    crossinline executor: Executor<NumericInstruction.I32Add>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
