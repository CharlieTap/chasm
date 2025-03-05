package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32AddExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32AddDispatcher(
    instruction: FusedNumericInstruction.I32Add,
) = I32AddDispatcher(
    instruction = instruction,
    executor = ::I32AddExecutor,
)

internal inline fun I32AddDispatcher(
    instruction: FusedNumericInstruction.I32Add,
    crossinline executor: Executor<FusedNumericInstruction.I32Add>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
