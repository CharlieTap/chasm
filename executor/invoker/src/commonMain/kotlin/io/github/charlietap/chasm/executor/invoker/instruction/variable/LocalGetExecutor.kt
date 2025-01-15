package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalGetExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalGet,
) {
    val (stack) = context
    val frame = stack.peekFrame().bind()

    stack.push(frame.locals[instruction.localIdx])
}
