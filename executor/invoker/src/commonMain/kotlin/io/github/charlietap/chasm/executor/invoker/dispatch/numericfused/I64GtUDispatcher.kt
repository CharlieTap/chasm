package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64GtUDispatcher(
    instruction: FusedNumericInstruction.I64GtU,
) = I64GtUDispatcher(
    instruction = instruction,
    executor = ::I64GtUExecutor,
)

internal inline fun I64GtUDispatcher(
    instruction: FusedNumericInstruction.I64GtU,
    crossinline executor: Executor<FusedNumericInstruction.I64GtU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
