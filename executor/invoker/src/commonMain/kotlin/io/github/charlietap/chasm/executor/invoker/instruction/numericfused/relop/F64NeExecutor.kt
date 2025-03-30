package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F64NeExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Ne,
): InstructionPointer {
    val left = Double.fromBits(instruction.left(vstack))
    val right = Double.fromBits(instruction.right(vstack))

    instruction.destination(if (left != right) 1L else 0L, vstack)
    return ip + 1
}
