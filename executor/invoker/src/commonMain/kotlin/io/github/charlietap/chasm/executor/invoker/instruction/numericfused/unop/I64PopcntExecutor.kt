package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64PopcntExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Popcnt,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = operand.countOneBits().toLong()

    instruction.destination(result, stack)
}
