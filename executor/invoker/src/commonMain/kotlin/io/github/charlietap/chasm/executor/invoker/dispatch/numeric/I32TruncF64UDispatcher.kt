package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32TruncF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32TruncF64UDispatcher(
    instruction: NumericInstruction.I32TruncF64U,
) = I32TruncF64UDispatcher(
    instruction = instruction,
    executor = ::I32TruncF64UExecutor,
)

internal inline fun I32TruncF64UDispatcher(
    instruction: NumericInstruction.I32TruncF64U,
    crossinline executor: Executor<NumericInstruction.I32TruncF64U>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
