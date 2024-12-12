package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncSatF64SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64TruncSatF64SDispatcher(
    instruction: NumericInstruction.I64TruncSatF64S,
) = I64TruncSatF64SDispatcher(
    instruction = instruction,
    executor = ::I64TruncSatF64SExecutor,
)

internal inline fun I64TruncSatF64SDispatcher(
    instruction: NumericInstruction.I64TruncSatF64S,
    crossinline executor: Executor<NumericInstruction.I64TruncSatF64S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
