package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefNullExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

fun RefNullDispatcher(
    instruction: ReferenceInstruction.RefNull,
) = RefNullDispatcher(
    instruction = instruction,
    executor = ::RefNullExecutor,
)

internal inline fun RefNullDispatcher(
    instruction: ReferenceInstruction.RefNull,
    crossinline executor: Executor<ReferenceInstruction.RefNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
