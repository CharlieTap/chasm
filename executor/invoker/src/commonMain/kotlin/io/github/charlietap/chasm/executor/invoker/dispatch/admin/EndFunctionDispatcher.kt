package io.github.charlietap.chasm.executor.invoker.dispatch.admin

import io.github.charlietap.chasm.executor.invoker.function.FinishFrameSlotCallResult
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun EndFunctionDispatcher(
    instruction: AdminInstruction.EndFunction,
): DispatchableInstruction = DispatchableInstruction { vstack, cstack, _, _, _ ->
    val frame = cstack.popFrame()
    cstack.shrinkHandlers(frame.handlerDepth)
    FinishFrameSlotCallResult(vstack, frame)
    frame.returnIp
}
