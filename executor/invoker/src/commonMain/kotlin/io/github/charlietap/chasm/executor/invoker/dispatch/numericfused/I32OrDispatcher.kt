package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop.I32OrExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32OrDispatcher(
    instruction: FusedNumericInstruction.I32Or,
) = I32OrDispatcher(
    instruction = instruction,
    executor = ::I32OrExecutor,
)

internal inline fun I32OrDispatcher(
    instruction: FusedNumericInstruction.I32Or,
    crossinline executor: Executor<FusedNumericInstruction.I32Or>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
