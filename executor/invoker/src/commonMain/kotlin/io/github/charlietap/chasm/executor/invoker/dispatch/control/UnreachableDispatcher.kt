package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.UnreachableExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun UnreachableDispatcher(
    instruction: ControlInstruction.Unreachable,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    UnreachableExecutor(vstack, context, instruction)
    nextIp
}
