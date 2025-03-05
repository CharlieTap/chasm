package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

internal inline fun ReturnExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Return,
) {
    val cstack = context.cstack
    val vstack = context.vstack

    val frame = cstack.popFrame()

    val depths = frame.depths
    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)
    vstack.shrink(frame.arity, depths.values)

    vstack.framePointer = frame.previousFramePointer
}
