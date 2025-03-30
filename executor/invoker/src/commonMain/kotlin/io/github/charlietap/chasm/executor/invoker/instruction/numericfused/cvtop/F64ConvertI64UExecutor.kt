package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun F64ConvertI64UExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64U,
): InstructionPointer {
    val operand = instruction.operand(vstack)
    val result = operand.convertF64u().toRawBits()

    instruction.destination(result, vstack)
    return ip + 1
}
