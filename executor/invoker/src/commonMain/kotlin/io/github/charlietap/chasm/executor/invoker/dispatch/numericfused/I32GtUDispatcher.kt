package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtU,
) = I32GtUDispatcher(
    instruction = instruction,
    executor = ::I32GtUExecutor,
)

internal inline fun I32GtUDispatcher(
    instruction: FusedNumericInstruction.I32GtU,
    crossinline executor: Executor<FusedNumericInstruction.I32GtU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
