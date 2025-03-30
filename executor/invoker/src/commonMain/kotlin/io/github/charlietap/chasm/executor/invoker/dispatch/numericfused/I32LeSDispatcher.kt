package io.github.charlietap.chasm.executor.invoker.dispatch.numericfused

import io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop.I32LeSExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeS,
) = I32LeSDispatcher(
    instruction = instruction,
    executor = ::I32LeSExecutor,
)

internal inline fun I32LeSDispatcher(
    instruction: FusedNumericInstruction.I32LeS,
    crossinline executor: Executor<FusedNumericInstruction.I32LeS>,
): DispatchableInstruction = { ip, vstack, cstack, store, context ->
    executor(ip, vstack, cstack, store, context, instruction)
}
