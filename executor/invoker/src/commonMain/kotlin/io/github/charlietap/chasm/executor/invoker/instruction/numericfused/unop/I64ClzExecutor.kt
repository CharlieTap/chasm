package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.executor.invoker.ext.countLeadingZero
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64ClzExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Clz,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = operand.countLeadingZero()

    instruction.destination(result, stack)
}
