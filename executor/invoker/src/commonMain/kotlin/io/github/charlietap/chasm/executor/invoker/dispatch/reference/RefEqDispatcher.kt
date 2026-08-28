package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefEqExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefEqDispatcher(
    instruction: ReferenceInstruction.RefEq,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefEqExecutor(vstack, context, instruction)
    nextIp
}
