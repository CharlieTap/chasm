package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun LocalTeeExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalTee,
): InstructionPointer {
    val value = instruction.operand(vstack)
    vstack.setLocal(instruction.localIdx, value)
    vstack.push(value)

    return ip + 1
}
