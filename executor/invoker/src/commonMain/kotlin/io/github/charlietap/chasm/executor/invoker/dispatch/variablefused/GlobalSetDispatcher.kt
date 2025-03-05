package io.github.charlietap.chasm.executor.invoker.dispatch.variablefused

import io.github.charlietap.chasm.executor.invoker.instruction.variablefused.GlobalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction

fun GlobalSetDispatcher(
    instruction: FusedVariableInstruction.GlobalSet,
) = GlobalSetDispatcher(
    instruction = instruction,
    executor = ::GlobalSetExecutor,
)

internal inline fun GlobalSetDispatcher(
    instruction: FusedVariableInstruction.GlobalSet,
    crossinline executor: Executor<FusedVariableInstruction.GlobalSet>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
