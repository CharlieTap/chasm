package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64UExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32TruncSatF64UDispatcher(
    instruction: NumericInstruction.I32TruncSatF64U,
) = I32TruncSatF64UDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF64UExecutor,
)

internal inline fun I32TruncSatF64UDispatcher(
    instruction: NumericInstruction.I32TruncSatF64U,
    crossinline executor: Executor<NumericInstruction.I32TruncSatF64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
