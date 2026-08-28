package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefAsNonNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefAsNonNullDispatcher(
    instruction: ReferenceInstruction.RefAsNonNull,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefAsNonNullExecutor(vstack, context, instruction)
    nextIp
}
