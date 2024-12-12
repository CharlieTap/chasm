package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF32SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32TruncF32SDispatcher(
    instruction: NumericInstruction.I32TruncF32S,
) = I32TruncF32SDispatcher(
    instruction = instruction,
    executor = ::I32TruncF32SExecutor,
)

internal inline fun I32TruncF32SDispatcher(
    instruction: NumericInstruction.I32TruncF32S,
    crossinline executor: Executor<NumericInstruction.I32TruncF32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
