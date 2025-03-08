package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop.I32TruncF64SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64S,
) = I32TruncF64SDispatcher(
    instruction = instruction,
    executor = ::I32TruncF64SExecutor,
)

internal inline fun I32TruncF64SDispatcher(
    instruction: FusedNumericInstruction.I32TruncF64S,
    crossinline executor: Executor<FusedNumericInstruction.I32TruncF64S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
