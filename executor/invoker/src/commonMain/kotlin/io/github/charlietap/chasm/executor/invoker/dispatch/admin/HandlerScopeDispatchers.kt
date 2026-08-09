package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun PushHandlerDispatcher(
    instruction: AdminInstruction.PushHandler,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, _, _, nextIp ->
    cstack.push(
        ExceptionHandler(
            handlers = instruction.handlers,
            payloadDestinationSlots = instruction.payloadDestinationSlots,
            continuationIps = instruction.continuationIps,
            framesDepth = cstack.framesDepth(),
            framePointer = vstack.framePointer,
            valueDepth = vstack.depth(),
        ),
    )
    nextIp
}

fun PopHandlerDispatcher(
    instruction: AdminInstruction.PopHandler,
): DispatchableInstruction = DispatchableInstruction { _, cstack, _, _, nextIp ->
    cstack.popHandler()
    nextIp
}
