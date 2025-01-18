package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext

internal inline fun FrameInstructionExecutor(
    context: ExecutionContext,
) {
    val frame = context.stack.popFrame()
    context.stack.shrinkValues(frame.arity, frame.depths.values)
    context.stack.setFramePointer(frame.previousFramePointer)
}
