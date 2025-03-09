package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefCastExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

fun RefCastDispatcher(
    instruction: FusedReferenceInstruction.RefCast,
) = RefCastDispatcher(
    instruction = instruction,
    executor = ::RefCastExecutor,
)

internal inline fun RefCastDispatcher(
    instruction: FusedReferenceInstruction.RefCast,
    crossinline executor: Executor<FusedReferenceInstruction.RefCast>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
