package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64TruncSatF32SDispatcher(
    instruction: NumericInstruction.I64TruncSatF32S,
) = I64TruncSatF32SDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF32SExecutor,
)

internal inline fun I64TruncSatF32SDispatcher(
    instruction: NumericInstruction.I64TruncSatF32S,
    crossinline executor: Executor<NumericInstruction.I64TruncSatF32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
