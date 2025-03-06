package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LtUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64LtUDispatcher(
    instruction: FusedNumericInstruction.I64LtU,
) = I64LtUDispatcher(
    instruction = instruction,
    executor = ::I64LtUExecutor,
)

internal inline fun I64LtUDispatcher(
    instruction: FusedNumericInstruction.I64LtU,
    crossinline executor: Executor<FusedNumericInstruction.I64LtU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
