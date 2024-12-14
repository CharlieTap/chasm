package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32UExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64TruncSatF32UDispatcher(
    instruction: NumericInstruction.I64TruncSatF32U,
) = I64TruncSatF32UDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF32UExecutor,
)

internal inline fun I64TruncSatF32UDispatcher(
    instruction: NumericInstruction.I64TruncSatF32U,
    crossinline executor: Executor<NumericInstruction.I64TruncSatF32U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
