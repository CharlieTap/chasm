package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.I64ExtendI32SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64ExtendI32SDispatcher(
    instruction: NumericInstruction.I64ExtendI32S,
) = I64ExtendI32SDispatcher(
    instruction = instruction,
    executor = ::I64ExtendI32SExecutor,
)

internal inline fun I64ExtendI32SDispatcher(
    instruction: NumericInstruction.I64ExtendI32S,
    crossinline executor: Executor<NumericInstruction.I64ExtendI32S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
