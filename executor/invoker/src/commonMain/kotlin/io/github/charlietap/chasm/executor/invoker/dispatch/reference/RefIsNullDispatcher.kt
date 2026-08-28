package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefIsNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefIsNullDispatcher(
    instruction: ReferenceInstruction.RefIsNull,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefIsNullExecutor(vstack, context, instruction)
    nextIp
}
