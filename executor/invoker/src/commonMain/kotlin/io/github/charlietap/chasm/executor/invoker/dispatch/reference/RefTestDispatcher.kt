package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefTestExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTest,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefTestExecutor(vstack, context, instruction)
    nextIp
}
