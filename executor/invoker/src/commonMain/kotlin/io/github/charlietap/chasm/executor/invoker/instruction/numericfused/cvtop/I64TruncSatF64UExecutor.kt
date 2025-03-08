package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.executor.invoker.ext.truncI64u
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64TruncSatF64UExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64TruncSatF64U,
) {
    val stack = context.vstack

    val operand = Double.fromBits(instruction.operand(stack))
    val result = operand.truncI64u()

    instruction.destination(result, stack)
}
