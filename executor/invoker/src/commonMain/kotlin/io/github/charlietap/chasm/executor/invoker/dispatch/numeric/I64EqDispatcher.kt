package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.relop.I64EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64EqDispatcher(
    instruction: NumericInstruction.I64Eq,
) = I64EqDispatcher(
    instruction = instruction,
    executor = ::I64EqExecutor,
)

internal inline fun I64EqDispatcher(
    instruction: NumericInstruction.I64Eq,
    crossinline executor: Executor<NumericInstruction.I64Eq>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
