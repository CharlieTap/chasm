package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64UExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64TruncF64UDispatcher(
    instruction: NumericInstruction.I64TruncF64U,
) = I64TruncF64UDispatcher(
    instruction = instruction,
    executor = ::I64TruncF64UExecutor,
)

internal inline fun I64TruncF64UDispatcher(
    instruction: NumericInstruction.I64TruncF64U,
    crossinline executor: Executor<NumericInstruction.I64TruncF64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
