package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.F64MaxExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun F64MaxDispatcher(
    instruction: NumericInstruction.F64Max,
) = F64MaxDispatcher(
    instruction = instruction,
    executor = ::F64MaxExecutor,
)

internal inline fun F64MaxDispatcher(
    instruction: NumericInstruction.F64Max,
    crossinline executor: Executor<NumericInstruction.F64Max>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}