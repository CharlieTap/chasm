package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.function.FinishFrameSlotCallResult
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun EndFunctionInstructionExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.EndFunction,
) {
    val frame = cstack.popFrame()
    val depths = frame.depths

    cstack.shrinkHandlers(depths.handlers)
    cstack.shrinkInstructions(depths.instructions)
    cstack.shrinkLabels(depths.labels)

    FinishFrameSlotCallResult(vstack, frame)
}
