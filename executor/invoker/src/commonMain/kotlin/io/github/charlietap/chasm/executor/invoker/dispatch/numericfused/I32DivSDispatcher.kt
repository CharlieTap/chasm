package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32DivSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32DivSDispatcher(
    instruction: FusedNumericInstruction.I32DivS,
) = I32DivSDispatcher(
    instruction = instruction,
    executor = ::I32DivSExecutor,
)

internal inline fun I32DivSDispatcher(
    instruction: FusedNumericInstruction.I32DivS,
    crossinline executor: Executor<FusedNumericInstruction.I32DivS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
