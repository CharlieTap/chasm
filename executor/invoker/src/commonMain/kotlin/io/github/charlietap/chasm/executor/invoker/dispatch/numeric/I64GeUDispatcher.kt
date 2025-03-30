package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeU,
) = I64GeUDispatcher(
    instruction = instruction,
    executor = ::I64GeUExecutor,
)

internal inline fun I64GeUDispatcher(
    instruction: NumericInstruction.I64GeU,
    crossinline executor: Executor<NumericInstruction.I64GeU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
