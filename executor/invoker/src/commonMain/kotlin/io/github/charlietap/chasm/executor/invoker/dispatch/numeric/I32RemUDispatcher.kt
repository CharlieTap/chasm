package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RemUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemU,
) = I32RemUDispatcher(
    instruction = instruction,
    executor = ::I32RemUExecutor,
)

internal inline fun I32RemUDispatcher(
    instruction: NumericInstruction.I32RemU,
    crossinline executor: Executor<NumericInstruction.I32RemU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
