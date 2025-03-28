package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32DivUDispatcher(
    instruction: FusedNumericInstruction.I32DivU,
) = I32DivUDispatcher(
    instruction = instruction,
    executor = ::I32DivUExecutor,
)

internal inline fun I32DivUDispatcher(
    instruction: FusedNumericInstruction.I32DivU,
    crossinline executor: Executor<FusedNumericInstruction.I32DivU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
