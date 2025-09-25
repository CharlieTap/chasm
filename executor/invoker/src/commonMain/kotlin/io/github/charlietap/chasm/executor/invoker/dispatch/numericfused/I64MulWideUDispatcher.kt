package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64MulWideUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64MulWideUDispatcher(
    instruction: FusedNumericInstruction.I64MulWideU,
) = I64MulWideUDispatcher(
    instruction = instruction,
    executor = ::I64MulWideUExecutor,
)

internal inline fun I64MulWideUDispatcher(
    instruction: FusedNumericInstruction.I64MulWideU,
    crossinline executor: Executor<FusedNumericInstruction.I64MulWideU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
