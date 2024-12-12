package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64CtzExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64CtzDispatcher(
    instruction: NumericInstruction.I64Ctz,
) = I64CtzDispatcher(
    instruction = instruction,
    executor = ::I64CtzExecutor,
)

internal inline fun I64CtzDispatcher(
    instruction: NumericInstruction.I64Ctz,
    crossinline executor: Executor<NumericInstruction.I64Ctz>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
