package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnCallRefExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnCallRefDispatcher(
    instruction: ControlInstruction.ReturnCallRef,
): DispatchableInstruction = { vstack, cstack, store, context, nextIp ->
    ReturnCallRefExecutor(vstack, cstack, store, context, instruction)
}
