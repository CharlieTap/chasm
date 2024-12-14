package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeSExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeS,
) = I32GeSDispatcher(
    instruction = instruction,
    executor = ::I32GeSExecutor,
)

internal inline fun I32GeSDispatcher(
    instruction: NumericInstruction.I32GeS,
    crossinline executor: Executor<NumericInstruction.I32GeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
