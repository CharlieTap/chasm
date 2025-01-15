package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popFrame

internal inline fun FrameInstructionExecutor(
    context: ExecutionContext,
) {
    context.stack.popFrame().bind()
}
