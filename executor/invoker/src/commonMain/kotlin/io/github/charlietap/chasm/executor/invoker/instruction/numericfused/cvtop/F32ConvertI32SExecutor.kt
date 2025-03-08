package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.convertF32s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32ConvertI32SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32ConvertI32S,
) {
    val stack = context.vstack

    val operand = instruction.operand(stack).toInt()
    val result = operand.convertF32s().toRawBits().toLong()

    instruction.destination(result, stack)
}
