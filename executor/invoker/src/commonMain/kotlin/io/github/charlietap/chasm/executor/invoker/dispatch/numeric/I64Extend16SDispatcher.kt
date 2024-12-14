package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend16SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64Extend16SDispatcher(
    instruction: NumericInstruction.I64Extend16S,
) = I64Extend16SDispatcher(
    instruction = instruction,
    executor = ::I64Extend16SExecutor,
)

internal inline fun I64Extend16SDispatcher(
    instruction: NumericInstruction.I64Extend16S,
    crossinline executor: Executor<NumericInstruction.I64Extend16S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
