package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend16SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32Extend16SDispatcher(
    instruction: NumericInstruction.I32Extend16S,
) = I32Extend16SDispatcher(
    instruction = instruction,
    executor = ::I32Extend16SExecutor,
)

internal inline fun I32Extend16SDispatcher(
    instruction: NumericInstruction.I32Extend16S,
    crossinline executor: Executor<NumericInstruction.I32Extend16S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
