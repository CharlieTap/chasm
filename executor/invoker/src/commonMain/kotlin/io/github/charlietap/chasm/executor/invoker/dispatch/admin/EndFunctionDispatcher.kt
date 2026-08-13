package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.function.FinishFrameSlotCallResult
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun EndFunctionDispatcher(
    instruction: AdminInstruction.EndFunction,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, _, _, _ ->
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
    returnIp
}
