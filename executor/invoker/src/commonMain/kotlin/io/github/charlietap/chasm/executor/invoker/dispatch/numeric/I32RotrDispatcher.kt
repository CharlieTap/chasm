package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotrExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32Rotr,
) = I32RotrDispatcher(
    instruction = instruction,
    executor = ::I32RotrExecutor,
)

internal inline fun I32RotrDispatcher(
    instruction: NumericInstruction.I32Rotr,
    crossinline executor: Executor<NumericInstruction.I32Rotr>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
