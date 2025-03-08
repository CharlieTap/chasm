package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.cvtop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64PromoteF32Executor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64PromoteF32,
) {
    val stack = context.vstack

    val operand = Float.fromBits(instruction.operand(stack).toInt())
    val result = operand.toDouble().toRawBits()

    instruction.destination(result, stack)
}
