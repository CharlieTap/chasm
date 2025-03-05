package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefIsNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefIsNullDispatcher(
    instruction: ReferenceInstruction.RefIsNull,
) = RefIsNullDispatcher(
    instruction = instruction,
    executor = ::RefIsNullExecutor,
)

internal inline fun RefIsNullDispatcher(
    instruction: ReferenceInstruction.RefIsNull,
    crossinline executor: Executor<ReferenceInstruction.RefIsNull>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
