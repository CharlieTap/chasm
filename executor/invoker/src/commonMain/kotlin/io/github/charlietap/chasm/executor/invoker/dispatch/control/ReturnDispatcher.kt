package io.github.charlietap.chasm.executor.invoker.dispatch.control

import io.github.charlietap.chasm.executor.invoker.instruction.control.ReturnExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun ReturnDispatcher(
    instruction: ControlInstruction.Return,
): DispatchableInstruction = { vstack, cstack, _, _, _ ->
    ReturnExecutor(vstack, cstack)
}
