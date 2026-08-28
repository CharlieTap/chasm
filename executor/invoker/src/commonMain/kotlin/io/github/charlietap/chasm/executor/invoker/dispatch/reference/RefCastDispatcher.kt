package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefCastExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefCastDispatcher(
    instruction: ReferenceInstruction.RefCast,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefCastExecutor(vstack, context, instruction)
    nextIp
}
