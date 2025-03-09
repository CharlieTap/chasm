package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I64OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I64OrDispatcher(
    instruction: FusedNumericInstruction.I64Or,
) = I64OrDispatcher(
    instruction = instruction,
    executor = ::I64OrExecutor,
)

internal inline fun I64OrDispatcher(
    instruction: FusedNumericInstruction.I64Or,
    crossinline executor: Executor<FusedNumericInstruction.I64Or>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
