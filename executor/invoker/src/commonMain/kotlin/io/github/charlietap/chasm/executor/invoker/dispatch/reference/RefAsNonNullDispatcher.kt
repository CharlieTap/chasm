package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefAsNonNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.Executor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefAsNonNullDispatcher(
    instruction: ReferenceInstruction.RefAsNonNull,
) = RefAsNonNullDispatcher(
    instruction = instruction,
    executor = ::RefAsNonNullExecutor,
)

internal inline fun RefAsNonNullDispatcher(
    instruction: ReferenceInstruction.RefAsNonNull,
    crossinline executor: Executor<ReferenceInstruction.RefAsNonNull>,
): DispatchableInstruction = { vstack, cstack, store, context ->
    executor(vstack, cstack, store, context, instruction)
}
