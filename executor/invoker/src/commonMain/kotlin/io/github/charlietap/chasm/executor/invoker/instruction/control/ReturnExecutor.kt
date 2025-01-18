package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun ReturnExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Return,
) {
    val (stack) = context
    val frame = stack.popFrame()

    val depths = frame.depths
    stack.shrinkHandlers(0, depths.handlers)
    stack.shrinkInstructions(0, depths.instructions)
    stack.shrinkLabels(0, depths.labels)
    stack.shrinkValues(frame.arity, depths.values)

    stack.setFramePointer(frame.previousFramePointer)
}
