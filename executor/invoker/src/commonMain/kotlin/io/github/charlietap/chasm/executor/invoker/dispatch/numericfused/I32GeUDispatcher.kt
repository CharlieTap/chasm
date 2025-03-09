package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeU,
) = I32GeUDispatcher(
    instruction = instruction,
    executor = ::I32GeUExecutor,
)

internal inline fun I32GeUDispatcher(
    instruction: FusedNumericInstruction.I32GeU,
    crossinline executor: Executor<FusedNumericInstruction.I32GeU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
