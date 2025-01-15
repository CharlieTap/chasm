package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.instruction
import io.github.charlietap.chasm.executor.runtime.ext.popFunctionAddress
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun CallRefExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.CallRef,
) {
    val (stack, store) = context
    val value = stack.popFunctionAddress().bind()

    val instruction = store.instruction(value.address).bind()
    instruction(context)
}
