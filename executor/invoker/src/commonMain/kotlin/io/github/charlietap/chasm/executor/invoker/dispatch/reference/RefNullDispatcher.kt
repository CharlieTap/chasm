package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefNullExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefNullDispatcher(
    instruction: ReferenceInstruction.RefNull,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefNullExecutor(vstack, context, instruction)
    nextIp
}
