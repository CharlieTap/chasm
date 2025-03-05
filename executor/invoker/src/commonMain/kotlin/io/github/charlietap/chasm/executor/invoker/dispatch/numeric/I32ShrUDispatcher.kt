package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32ShrUExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrU,
) = I32ShrUDispatcher(
    instruction = instruction,
    executor = ::I32ShrUExecutor,
)

internal inline fun I32ShrUDispatcher(
    instruction: NumericInstruction.I32ShrU,
    crossinline executor: Executor<NumericInstruction.I32ShrU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
