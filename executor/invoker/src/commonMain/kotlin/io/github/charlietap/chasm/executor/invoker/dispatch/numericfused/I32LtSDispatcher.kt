package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtS,
) = I32LtSDispatcher(
    instruction = instruction,
    executor = ::I32LtSExecutor,
)

internal inline fun I32LtSDispatcher(
    instruction: FusedNumericInstruction.I32LtS,
    crossinline executor: Executor<FusedNumericInstruction.I32LtS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
