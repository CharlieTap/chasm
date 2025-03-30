package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32EqExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Eq,
): InstructionPointer {
    val left = Float.fromBits(instruction.left(vstack).toInt())
    val right = Float.fromBits(instruction.right(vstack).toInt())

    instruction.destination(if (left == right) 1L else 0L, vstack)

    return ip + 1
}
