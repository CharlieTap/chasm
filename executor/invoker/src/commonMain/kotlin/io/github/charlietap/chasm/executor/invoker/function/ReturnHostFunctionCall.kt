package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.NO_RESULT_SLOT_BASE
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun ReturnHostFunctionCall(
    vstack: ValueStack,
    cstack: ControlStack,
    context: ExecutionContext,
    function: FunctionInstance.HostFunction,
): Int {
    HostFunctionCall(vstack, context, cstack.frameInstance(), function)

    val resultCount = function.functionType.results.types.size
    val handlerDepth = cstack.frameHandlerDepth()
    val valueDepth = cstack.frameValueDepth()
    val previousFramePointer = cstack.framePreviousFramePointer()
    val resultSlotBase = cstack.frameResultSlotBase()
    val returnIp = cstack.frameReturnIp()
    cstack.discardFrame()
    cstack.shrinkHandlers(handlerDepth)

    vstack.shrink(preserveTopN = resultCount, depth = valueDepth)
    if (resultSlotBase != NO_RESULT_SLOT_BASE) {
        vstack.copySlots(
            source = valueDepth,
            destination = previousFramePointer + resultSlotBase,
            count = resultCount,
        )
        vstack.shrink(preserveTopN = 0, depth = valueDepth)
    }
    vstack.framePointer = previousFramePointer
    return returnIp
}
