package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F32DemoteF64Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DemoteF64,
): InstructionPointer {
    val operand = Double.fromBits(instruction.operand(vstack))
    val result = operand.toFloat().toRawBits().toLong()

    instruction.destination(result, vstack)

    return ip + 1
}
