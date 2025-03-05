package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32EqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32Eq,
) = I32EqDispatcher(
    instruction = instruction,
    executor = ::I32EqExecutor,
)

internal inline fun I32EqDispatcher(
    instruction: FusedNumericInstruction.I32Eq,
    crossinline executor: Executor<FusedNumericInstruction.I32Eq>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
