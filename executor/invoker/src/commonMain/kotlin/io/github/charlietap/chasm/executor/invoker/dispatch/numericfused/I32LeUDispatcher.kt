package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeU,
) = I32LeUDispatcher(
    instruction = instruction,
    executor = ::I32LeUExecutor,
)

internal inline fun I32LeUDispatcher(
    instruction: FusedNumericInstruction.I32LeU,
    crossinline executor: Executor<FusedNumericInstruction.I32LeU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
