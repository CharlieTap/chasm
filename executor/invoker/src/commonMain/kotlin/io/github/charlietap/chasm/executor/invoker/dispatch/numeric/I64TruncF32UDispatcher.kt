package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF32UExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64TruncF32UDispatcher(
    instruction: NumericInstruction.I64TruncF32U,
) = I64TruncF32UDispatcher(
    instruction = instruction,
    executor = ::I64TruncF32UExecutor,
)

internal inline fun I64TruncF32UDispatcher(
    instruction: NumericInstruction.I64TruncF32U,
    crossinline executor: Executor<NumericInstruction.I64TruncF32U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
