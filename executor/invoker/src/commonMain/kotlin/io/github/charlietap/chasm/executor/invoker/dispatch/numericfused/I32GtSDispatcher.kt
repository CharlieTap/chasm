package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtS,
) = I32GtSDispatcher(
    instruction = instruction,
    executor = ::I32GtSExecutor,
)

internal inline fun I32GtSDispatcher(
    instruction: FusedNumericInstruction.I32GtS,
    crossinline executor: Executor<FusedNumericInstruction.I32GtS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
