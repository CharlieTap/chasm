package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.executor.invoker.ext.countLeadingZero
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64ClzExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Clz,
): InstructionPointer {
    val operand = instruction.operand(vstack)
    val result = operand.countLeadingZero()

    instruction.destination(result, vstack)

    return ip + 1
}
