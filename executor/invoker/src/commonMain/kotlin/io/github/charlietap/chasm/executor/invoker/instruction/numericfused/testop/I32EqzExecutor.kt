package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.testop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32EqzExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Eqz,
) {
    val stack = context.vstack
    val operand = instruction.operand(stack)
    val result = ((operand or -operand) ushr 63) xor 1L
    instruction.destination(result, stack)
}
