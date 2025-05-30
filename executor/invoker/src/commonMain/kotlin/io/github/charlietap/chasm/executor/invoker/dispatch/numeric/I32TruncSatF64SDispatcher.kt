package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncSatF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncSatF64SDispatcher(
    instruction: NumericInstruction.I32TruncSatF64S,
) = I32TruncSatF64SDispatcher(
    instruction = instruction,
    executor = ::I32TruncSatF64SExecutor,
)

internal inline fun I32TruncSatF64SDispatcher(
    instruction: NumericInstruction.I32TruncSatF64S,
    crossinline executor: Executor<NumericInstruction.I32TruncSatF64S>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
