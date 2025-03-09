package io.github.charlietap.chasm.executor.invoker.dispatch.variable

import io.github.charlietap.chasm.executor.invoker.instruction.variable.GlobalSetExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.VariableInstruction

fun GlobalSetDispatcher(
    instruction: VariableInstruction.GlobalSet,
) = GlobalSetDispatcher(
    instruction = instruction,
    executor = ::GlobalSetExecutor,
)

internal inline fun GlobalSetDispatcher(
    instruction: VariableInstruction.GlobalSet,
    crossinline executor: Executor<VariableInstruction.GlobalSet>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
