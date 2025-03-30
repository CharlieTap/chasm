package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32ShrUDispatcher(
    instruction: FusedNumericInstruction.I32ShrU,
) = I32ShrUDispatcher(
    instruction = instruction,
    executor = ::I32ShrUExecutor,
)

internal inline fun I32ShrUDispatcher(
    instruction: FusedNumericInstruction.I32ShrU,
    crossinline executor: Executor<FusedNumericInstruction.I32ShrU>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
