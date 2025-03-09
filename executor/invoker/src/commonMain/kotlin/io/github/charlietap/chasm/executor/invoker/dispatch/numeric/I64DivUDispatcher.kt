package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64DivUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivU,
) = I64DivUDispatcher(
    instruction = instruction,
    executor = ::I64DivUExecutor,
)

internal inline fun I64DivUDispatcher(
    instruction: NumericInstruction.I64DivU,
    crossinline executor: Executor<NumericInstruction.I64DivU>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
