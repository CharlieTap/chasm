package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeS,
) = I64LeSDispatcher(
    instruction = instruction,
    executor = ::I64LeSExecutor,
)

internal inline fun I64LeSDispatcher(
    instruction: NumericInstruction.I64LeS,
    crossinline executor: Executor<NumericInstruction.I64LeS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
