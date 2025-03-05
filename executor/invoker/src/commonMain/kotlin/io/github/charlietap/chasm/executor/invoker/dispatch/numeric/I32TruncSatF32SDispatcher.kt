package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF32SDispatcher(
    instruction: NumericInstruction.I32TruncSatF32S,
) = I32TruncSatF32SDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF32SExecutor,
)

internal inline fun I32TruncSatF32SDispatcher(
    instruction: NumericInstruction.I32TruncSatF32S,
    crossinline executor: Executor<NumericInstruction.I32TruncSatF32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
