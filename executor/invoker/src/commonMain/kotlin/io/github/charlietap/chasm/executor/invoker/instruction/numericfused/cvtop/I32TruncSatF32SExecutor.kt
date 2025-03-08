package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32s
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32TruncSatF32SExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32TruncSatF32S,
) {
    val stack = context.vstack

    val operand = Float.fromBits(instruction.operand(stack).toInt())
    val result = operand.truncI32s().toLong()

    instruction.destination(result, stack)
}
