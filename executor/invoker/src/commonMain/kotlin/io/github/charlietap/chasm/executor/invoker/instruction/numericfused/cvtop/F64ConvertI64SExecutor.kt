package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF64s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64ConvertI64SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64ConvertI64S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack)
    val result = operand.convertF64s().toRawBits()

    instruction.destination(result, stack)
}
