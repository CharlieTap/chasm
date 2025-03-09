package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Return,
) {
    val frame = cstack.popFrame()

    val depths = frame.depths
    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)
    vstack.shrink(frame.arity, depths.values)

    vstack.framePointer = frame.previousFramePointer
}
