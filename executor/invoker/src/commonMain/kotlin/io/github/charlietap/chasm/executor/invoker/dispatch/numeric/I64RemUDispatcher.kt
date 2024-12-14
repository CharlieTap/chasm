package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RemUExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemU,
) = I64RemUDispatcher(
    instruction = instruction,
    executor = ::I64RemUExecutor,
)

internal inline fun I64RemUDispatcher(
    instruction: NumericInstruction.I64RemU,
    crossinline executor: Executor<NumericInstruction.I64RemU>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}