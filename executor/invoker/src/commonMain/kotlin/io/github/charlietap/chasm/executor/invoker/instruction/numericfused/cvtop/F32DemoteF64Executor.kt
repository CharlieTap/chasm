package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32DemoteF64Executor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32DemoteF64,
) {
    val stack = context.vstack

    val operand = Double.fromBits(instruction.operand(stack))
    val result = operand.toFloat().toRawBits().toLong()

    instruction.destination(result, stack)
}
