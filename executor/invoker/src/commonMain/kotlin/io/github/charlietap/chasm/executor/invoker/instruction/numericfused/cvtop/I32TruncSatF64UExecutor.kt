package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI32u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I32TruncSatF64UExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32TruncSatF64U,
) {
    val stack = context.vstack

    val operand = Double.fromBits(instruction.operand(stack))
    val result = operand.truncI32u().toLong()

    instruction.destination(result, stack)
}
