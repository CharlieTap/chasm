package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun ReturnExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Return,
) {
    val cstack = context.cstack
    val vstack = context.vstack

    val frame = cstack.popFrame()

    val depths = frame.depths
    cstack.shrinkHandlers(0, depths.handlers)
    cstack.shrinkInstructions(0, depths.instructions)
    cstack.shrinkLabels(0, depths.labels)
    vstack.shrink(frame.arity, depths.values)

    vstack.framePointer = frame.previousFramePointer
}
