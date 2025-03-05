package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext

internal inline fun LabelInstructionExecutor(
    context: ExecutionContext,
) {
    context.cstack.popLabel()
}
