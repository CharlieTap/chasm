package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64LtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtS,
) = I64LtSDispatcher(
    instruction = instruction,
    executor = ::I64LtSExecutor,
)

internal inline fun I64LtSDispatcher(
    instruction: NumericInstruction.I64LtS,
    crossinline executor: Executor<NumericInstruction.I64LtS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
