package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32ConvertI32UExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32U,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.convertF32u().toRawBits().toLong()

    instruction.destination(result, stack)
}
