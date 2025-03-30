package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32LtUExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32LtU,
): InstructionPointer {
    val result = if (instruction.left(vstack).toULong() < instruction.right(vstack).toULong()) 1L else 0L
    instruction.destination(result, vstack)
    return ip + 1
}
