package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64RotrExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64RotrDispatcher(
    instruction: NumericInstruction.I64Rotr,
) = I64RotrDispatcher(
    instruction = instruction,
    executor = ::I64RotrExecutor,
)

internal inline fun I64RotrDispatcher(
    instruction: NumericInstruction.I64Rotr,
    crossinline executor: Executor<NumericInstruction.I64Rotr>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
