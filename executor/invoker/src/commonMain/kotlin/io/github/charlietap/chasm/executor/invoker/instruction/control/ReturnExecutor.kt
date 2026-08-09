package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.FinishFrameSlotCallResult
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ReturnExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
): Int {
    val frame = cstack.popFrame()

    cstack.shrinkHandlers(frame.handlerDepth)

    FinishFrameSlotCallResult(vstack, frame)
    return frame.returnIp
}
