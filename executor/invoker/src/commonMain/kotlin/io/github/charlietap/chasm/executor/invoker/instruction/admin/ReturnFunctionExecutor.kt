package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun ReturnFunctionExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.ReturnFunction,
): InstructionPointer {

    val frame = cstack.popFrame()
    val depths = frame.depths
    val adjustment = instruction.adjustment
    cstack.shrinkHandlers(depths.handlers)
    vstack.shrink(adjustment.keep, adjustment.depth)

    vstack.framePointer = frame.previousFramePointer
    context.setInstructions(frame.previousInstructions)

    return frame.previousInstructionPointer + 1
}
