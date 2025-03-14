package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I32GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtS,
) = I32GtSDispatcher(
    instruction = instruction,
    executor = ::I32GtSExecutor,
)

internal inline fun I32GtSDispatcher(
    instruction: NumericInstruction.I32GtS,
    crossinline executor: Executor<NumericInstruction.I32GtS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
