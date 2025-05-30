package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32RotrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32RotrDispatcher(
    instruction: NumericInstruction.I32Rotr,
) = I32RotrDispatcher(
    instruction = instruction,
    executor = ::I32RotrExecutor,
)

internal inline fun I32RotrDispatcher(
    instruction: NumericInstruction.I32Rotr,
    crossinline executor: Executor<NumericInstruction.I32Rotr>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
