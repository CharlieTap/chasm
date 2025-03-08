package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.extendI64s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64ExtendI32SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64ExtendI32S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.extendI64s()

    instruction.destination(result, stack)
}
