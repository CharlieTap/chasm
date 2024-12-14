package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.I32Extend8SExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32Extend8SDispatcher(
    instruction: NumericInstruction.I32Extend8S,
) = I32Extend8SDispatcher(
    instruction = instruction,
    executor = ::I32Extend8SExecutor,
)

internal inline fun I32Extend8SDispatcher(
    instruction: NumericInstruction.I32Extend8S,
    crossinline executor: Executor<NumericInstruction.I32Extend8S>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
