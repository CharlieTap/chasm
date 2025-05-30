package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64ExtendI32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ExtendI32U,
) {
    val operand = instruction.operand(vstack).toInt()
    val result = operand.extendI64u()

    instruction.destination(result, vstack)
}
