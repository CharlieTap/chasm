package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.instruction
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun CallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
) {
    val (stack, store) = context
    val address = stack.popFunctionAddress()

    val instruction = store.instruction(address)
    instruction(context)
}
