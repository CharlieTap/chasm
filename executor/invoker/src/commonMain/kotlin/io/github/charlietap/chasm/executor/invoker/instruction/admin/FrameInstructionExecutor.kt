package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext

internal inline fun FrameInstructionExecutor(
    context: ExecutionContext,
) {
    val frame = context.cstack.popFrame()
    context.vstack.shrink(frame.arity, frame.depths.values)
    context.vstack.framePointer = frame.previousFramePointer
}
