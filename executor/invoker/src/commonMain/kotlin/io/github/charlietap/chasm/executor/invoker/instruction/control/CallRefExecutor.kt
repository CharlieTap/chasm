package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.instruction
import io.github.charlietap.chasm.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

internal fun CallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
) {
    val stack = context.vstack
    val store = context.store
    val address = stack.popFunctionAddress()

    val instruction = store.instruction(address)
    instruction(context)
}
