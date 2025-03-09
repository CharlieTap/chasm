package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtU,
) = I32LtUDispatcher(
    instruction = instruction,
    executor = ::I32LtUExecutor,
)

internal inline fun I32LtUDispatcher(
    instruction: FusedNumericInstruction.I32LtU,
    crossinline executor: Executor<FusedNumericInstruction.I32LtU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
