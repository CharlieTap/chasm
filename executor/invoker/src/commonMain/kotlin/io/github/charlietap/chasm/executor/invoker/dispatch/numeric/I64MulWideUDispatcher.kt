package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64MulWideUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideU,
) = I64MulWideUDispatcher(
    instruction = instruction,
    executor = ::I64MulWideUExecutor,
)

internal inline fun I64MulWideUDispatcher(
    instruction: NumericInstruction.I64MulWideU,
    crossinline executor: Executor<NumericInstruction.I64MulWideU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
