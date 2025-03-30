package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.invoker.ext.max
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32MaxExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Max,
): InstructionPointer {
    val left = Float.fromBits(instruction.left(vstack).toInt())
    val right = Float.fromBits(instruction.right(vstack).toInt())

    val result = left.max(right).toRawBits()

    instruction.destination(result.toLong(), vstack)

    return ip + 1
}
