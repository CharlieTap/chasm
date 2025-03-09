package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32RemUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32RemUDispatcher(
    instruction: FusedNumericInstruction.I32RemU,
) = I32RemUDispatcher(
    instruction = instruction,
    executor = ::I32RemUExecutor,
)

internal inline fun I32RemUDispatcher(
    instruction: FusedNumericInstruction.I32RemU,
    crossinline executor: Executor<FusedNumericInstruction.I32RemU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
