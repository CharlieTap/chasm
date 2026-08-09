package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ThrowRefExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ThrowRefDispatcher(
    instruction: ControlInstruction.ThrowRef,
): DispatchableInstruction = { vstack, cstack, store, _, _ ->
    ThrowRefExecutor(vstack, cstack, store, instruction)
}
