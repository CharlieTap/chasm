package io.github.charlietap.chasm.executor.invoker.instruction.variablefused

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun LocalTeeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedVariableInstruction.LocalTee,
) {
    val value = instruction.operand(vstack)
    vstack.setLocal(instruction.localIdx, value)
    vstack.push(value)
}
