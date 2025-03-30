package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LeUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64LeUDispatcher(
    instruction: FusedNumericInstruction.I64LeU,
) = I64LeUDispatcher(
    instruction = instruction,
    executor = ::I64LeUExecutor,
)

internal inline fun I64LeUDispatcher(
    instruction: FusedNumericInstruction.I64LeU,
    crossinline executor: Executor<FusedNumericInstruction.I64LeU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
