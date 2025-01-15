package io.github.charlietap.chasm.executor.invoker.instruction.variable

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekValue
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction

internal inline fun LocalTeeExecutor(
    context: ExecutionContext,
    instruction: VariableInstruction.LocalTee,
) {
    val (stack) = context

    val value = stack.peekValue().bind()
    val frame = stack.peekFrame()

    frame.locals[instruction.localIdx] = value
}
