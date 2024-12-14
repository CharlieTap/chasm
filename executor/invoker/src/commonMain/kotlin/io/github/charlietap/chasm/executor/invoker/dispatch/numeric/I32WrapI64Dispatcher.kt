package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I32WrapI64Executor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32WrapI64Dispatcher(
    instruction: NumericInstruction.I32WrapI64,
) = I32WrapI64Dispatcher(
    instruction = instruction,
    executor = ::I32WrapI64Executor,
)

internal inline fun I32WrapI64Dispatcher(
    instruction: NumericInstruction.I32WrapI64,
    crossinline executor: Executor<NumericInstruction.I32WrapI64>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
