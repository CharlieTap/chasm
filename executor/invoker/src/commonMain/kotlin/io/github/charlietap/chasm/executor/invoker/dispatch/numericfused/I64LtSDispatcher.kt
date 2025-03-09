package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64LtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64LtSDispatcher(
    instruction: FusedNumericInstruction.I64LtS,
) = I64LtSDispatcher(
    instruction = instruction,
    executor = ::I64LtSExecutor,
)

internal inline fun I64LtSDispatcher(
    instruction: FusedNumericInstruction.I64LtS,
    crossinline executor: Executor<FusedNumericInstruction.I64LtS>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
