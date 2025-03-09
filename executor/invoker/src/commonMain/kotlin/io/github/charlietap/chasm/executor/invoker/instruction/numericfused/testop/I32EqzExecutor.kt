package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32EqzExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Eqz,
) {
    val operand = instruction.operand(vstack)
    val result = ((operand or -operand) ushr 63) xor 1L

    instruction.destination(result, vstack)
}
