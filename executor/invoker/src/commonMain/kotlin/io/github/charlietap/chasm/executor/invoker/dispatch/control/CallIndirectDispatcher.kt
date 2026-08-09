package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.CallIndirectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun CallIndirectDispatcher(
    instruction: ControlInstruction.CallIndirect,
): DispatchableInstruction = { vstack, cstack, store, context, nextIp ->
    CallIndirectExecutor(vstack, cstack, store, context, instruction, nextIp)
}
