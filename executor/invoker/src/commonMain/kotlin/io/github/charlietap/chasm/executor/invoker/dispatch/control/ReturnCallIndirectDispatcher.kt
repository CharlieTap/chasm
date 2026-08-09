package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnCallIndirectExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnCallIndirectDispatcher(
    instruction: ControlInstruction.ReturnCallIndirect,
): DispatchableInstruction = { vstack, cstack, store, context, nextIp ->
    ReturnCallIndirectExecutor(vstack, cstack, store, context, instruction)
}
