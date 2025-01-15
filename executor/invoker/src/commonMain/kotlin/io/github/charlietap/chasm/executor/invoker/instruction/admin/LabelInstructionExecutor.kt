package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popLabel

internal inline fun LabelInstructionExecutor(
    context: ExecutionContext,
) {
    context.stack.popLabel().bind()
}
