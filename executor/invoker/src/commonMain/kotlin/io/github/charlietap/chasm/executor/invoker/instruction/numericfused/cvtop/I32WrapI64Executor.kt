package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.wrap
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32WrapI64Executor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32WrapI64,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = operand.wrap().toLong()

    instruction.destination(result, stack)
}
