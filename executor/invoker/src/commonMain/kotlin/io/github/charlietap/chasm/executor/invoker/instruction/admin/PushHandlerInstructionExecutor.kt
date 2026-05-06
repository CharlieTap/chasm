package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

@Suppress("UNUSED_PARAMETER")
internal inline fun PushHandlerInstructionExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.PushHandler,
) {
    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        payloadDestinationSlots = instruction.payloadDestinationSlots,
        continuations = instruction.continuations,
        continuationSource = instruction.continuationSource,
        continuationOffsets = instruction.continuationOffsets,
        framesDepth = cstack.framesDepth(),
        instructionsDepth = cstack.instructionsDepth() - instruction.discardCount,
        labelsDepth = cstack.labelsDepth(),
        framePointer = vstack.framePointer,
    )

    cstack.push(handler)
}
