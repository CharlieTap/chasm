package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun JumpTableExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpTable,
): InstructionPointer {

    val index = vstack.popI32()

    val offset: Int
    val adjustment: StackAdjustment

    if (index >= 0 && index < instruction.offsets.size) {
        offset = instruction.offsets[index]
        adjustment = instruction.adjustments[index]
    } else {
        offset = instruction.defaultOffset
        adjustment = instruction.defaultAdjustment
    }

    vstack.shrink(adjustment.keep, adjustment.depth)
    return ip + offset
}
