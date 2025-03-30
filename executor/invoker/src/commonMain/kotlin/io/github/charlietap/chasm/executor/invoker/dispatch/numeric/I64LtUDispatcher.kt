package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtU,
) = I64LtUDispatcher(
    instruction = instruction,
    executor = ::I64LtUExecutor,
)

internal inline fun I64LtUDispatcher(
    instruction: NumericInstruction.I64LtU,
    crossinline executor: Executor<NumericInstruction.I64LtU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
