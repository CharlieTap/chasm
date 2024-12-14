package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64TruncF64SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64TruncF64SDispatcher(
    instruction: NumericInstruction.I64TruncF64S,
) = I64TruncF64SDispatcher(
    instruction = instruction,
    executor = ::I64TruncF64SExecutor,
)

internal inline fun I64TruncF64SDispatcher(
    instruction: NumericInstruction.I64TruncF64S,
    crossinline executor: Executor<NumericInstruction.I64TruncF64S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}