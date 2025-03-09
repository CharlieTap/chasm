package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.I64OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction

fun I64OrDispatcher(
    instruction: NumericInstruction.I64Or,
) = I64OrDispatcher(
    instruction = instruction,
    executor = ::I64OrExecutor,
)

internal inline fun I64OrDispatcher(
    instruction: NumericInstruction.I64Or,
    crossinline executor: Executor<NumericInstruction.I64Or>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
