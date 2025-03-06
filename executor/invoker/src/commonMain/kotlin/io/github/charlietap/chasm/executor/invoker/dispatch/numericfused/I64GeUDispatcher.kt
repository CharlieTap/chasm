package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64GeUDispatcher(
    instruction: FusedNumericInstruction.I64GeU,
) = I64GeUDispatcher(
    instruction = instruction,
    executor = ::I64GeUExecutor,
)

internal inline fun I64GeUDispatcher(
    instruction: FusedNumericInstruction.I64GeU,
    crossinline executor: Executor<FusedNumericInstruction.I64GeU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
