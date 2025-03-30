package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I64GtSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64GtSDispatcher(
    instruction: FusedNumericInstruction.I64GtS,
) = I64GtSDispatcher(
    instruction = instruction,
    executor = ::I64GtSExecutor,
)

internal inline fun I64GtSDispatcher(
    instruction: FusedNumericInstruction.I64GtS,
    crossinline executor: Executor<FusedNumericInstruction.I64GtS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
