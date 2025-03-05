package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I32AndExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32AndDispatcher(
    instruction: NumericInstruction.I32And,
) = I32AndDispatcher(
    instruction = instruction,
    executor = ::I32AndExecutor,
)

internal inline fun I32AndDispatcher(
    instruction: NumericInstruction.I32And,
    crossinline executor: Executor<NumericInstruction.I32And>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
