package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64DivSExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

fun I64DivSDispatcher(
    instruction: FusedNumericInstruction.I64DivS,
) = I64DivSDispatcher(
    instruction = instruction,
    executor = ::I64DivSExecutor,
)

internal inline fun I64DivSDispatcher(
    instruction: FusedNumericInstruction.I64DivS,
    crossinline executor: Executor<FusedNumericInstruction.I64DivS>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
