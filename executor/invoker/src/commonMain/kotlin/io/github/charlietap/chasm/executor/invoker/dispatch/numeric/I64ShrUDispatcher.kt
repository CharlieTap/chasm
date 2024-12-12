package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64ShrUExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrU,
) = I64ShrUDispatcher(
    instruction = instruction,
    executor = ::I64ShrUExecutor,
)

internal inline fun I64ShrUDispatcher(
    instruction: NumericInstruction.I64ShrU,
    crossinline executor: Executor<NumericInstruction.I64ShrU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
