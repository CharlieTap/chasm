package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeU,
) = I32GeUDispatcher(
    instruction = instruction,
    executor = ::I32GeUExecutor,
)

internal inline fun I32GeUDispatcher(
    instruction: NumericInstruction.I32GeU,
    crossinline executor: Executor<NumericInstruction.I32GeU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
