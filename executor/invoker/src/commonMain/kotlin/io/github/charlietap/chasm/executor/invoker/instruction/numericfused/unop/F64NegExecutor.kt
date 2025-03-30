package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F64NegExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Neg,
): InstructionPointer {
    val double = Double.fromBits(instruction.operand(vstack))
    val result = (-double).toRawBits()

    instruction.destination(result, vstack)

    return ip + 1
}
