package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I64Extend32SExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64Extend32SDispatcher(
    instruction: NumericInstruction.I64Extend32S,
) = I64Extend32SDispatcher(
    instruction = instruction,
    executor = ::I64Extend32SExecutor,
)

internal inline fun I64Extend32SDispatcher(
    instruction: NumericInstruction.I64Extend32S,
    crossinline executor: Executor<NumericInstruction.I64Extend32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
