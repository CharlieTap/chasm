package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.CallRefExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun CallRefDispatcher(
    instruction: ControlInstruction.CallRef,
): DispatchableInstruction = { vstack, cstack, store, context, nextIp ->
    CallRefExecutor(vstack, cstack, store, context, instruction, nextIp)
}
