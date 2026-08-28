package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun PushHandlerDispatcher(
    instruction: AdminInstruction.PushHandler,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    context.cstack.push(
        ExceptionHandler(
            handlers = instruction.handlers,
            payloadDestinationSlots = instruction.payloadDestinationSlots,
            continuationIps = instruction.continuationIps,
            instance = instruction.instance,
            fp = vstack.fp,
            sp = vstack.sp,
        ),
    )
    nextIp
}

fun PopHandlerDispatcher(
    instruction: AdminInstruction.PopHandler,
): DispatchableInstruction = DispatchableInstruction { _, context, nextIp ->
    context.cstack.popHandler()
    nextIp
}
