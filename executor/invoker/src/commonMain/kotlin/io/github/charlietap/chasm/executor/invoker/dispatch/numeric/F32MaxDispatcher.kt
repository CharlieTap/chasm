package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F32MaxExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun F32MaxDispatcher(
    instruction: NumericInstruction.F32Max,
) = F32MaxDispatcher(
    instruction = instruction,
    executor = ::F32MaxExecutor,
)

internal inline fun F32MaxDispatcher(
    instruction: NumericInstruction.F32Max,
    crossinline executor: Executor<NumericInstruction.F32Max>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
