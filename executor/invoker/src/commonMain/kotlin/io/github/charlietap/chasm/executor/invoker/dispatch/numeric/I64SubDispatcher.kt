package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64SubExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.NumericInstruction

fun I64SubDispatcher(
    instruction: NumericInstruction.I64Sub,
) = I64SubDispatcher(
    instruction = instruction,
    executor = ::I64SubExecutor,
)

internal inline fun I64SubDispatcher(
    instruction: NumericInstruction.I64Sub,
    crossinline executor: Executor<NumericInstruction.I64Sub>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
