package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefCastExecutor
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction

fun RefCastDispatcher(
    instruction: ReferenceInstruction.RefCast,
) = RefCastDispatcher(
    instruction = instruction,
    executor = ::RefCastExecutor,
)

internal inline fun RefCastDispatcher(
    instruction: ReferenceInstruction.RefCast,
    crossinline executor: Executor<ReferenceInstruction.RefCast>,
): DispatchableInstruction = { context ->
    executor(context, instruction)
}
