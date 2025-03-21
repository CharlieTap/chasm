package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF32UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF32UDispatcher(
    instruction: NumericInstruction.I32TruncSatF32U,
) = I32TruncSatF32UDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF32UExecutor,
)

internal inline fun I32TruncSatF32UDispatcher(
    instruction: NumericInstruction.I32TruncSatF32U,
    crossinline executor: Executor<NumericInstruction.I32TruncSatF32U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
