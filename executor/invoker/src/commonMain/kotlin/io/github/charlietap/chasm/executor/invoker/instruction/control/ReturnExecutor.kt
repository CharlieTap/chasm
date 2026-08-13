package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.function.FinishFrameSlotCallResult
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ReturnExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
): Int {
    val resultArity = cstack.frameArity()
    val handlerDepth = cstack.frameHandlerDepth()
    val valueDepth = cstack.frameValueDepth()
    val previousFramePointer = cstack.framePreviousFramePointer()
    val resultSlotBase = cstack.frameResultSlotBase()
    val returnIp = cstack.frameReturnIp()
    cstack.discardFrame()

    cstack.shrinkHandlers(handlerDepth)

    FinishFrameSlotCallResult(
        vstack = vstack,
        resultArity = resultArity,
        valueDepth = valueDepth,
        callerFramePointer = previousFramePointer,
        resultSlotBase = resultSlotBase,
    )
    return returnIp
}
