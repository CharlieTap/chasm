package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF64UExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64U,
) = I32TruncF64UDispatcher(
    instruction = instruction,
    executor = ::I32TruncF64UExecutor,
)

internal inline fun I32TruncF64UDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64U,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncF64U>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
