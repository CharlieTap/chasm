package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun JumpIfExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpIf,
): InstructionPointer {

    val shouldBreak = vstack.pop() != 0L

    return if (shouldBreak) {
        vstack.shrink(instruction.adjustment.keep, instruction.adjustment.depth)
        ip + instruction.offset
    } else {
        ip + 1
    }
}
