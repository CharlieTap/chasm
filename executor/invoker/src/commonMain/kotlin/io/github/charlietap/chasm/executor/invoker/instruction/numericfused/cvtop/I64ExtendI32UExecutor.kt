package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64ExtendI32UExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ExtendI32U,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.extendI64u()

    instruction.destination(result, stack)
}
