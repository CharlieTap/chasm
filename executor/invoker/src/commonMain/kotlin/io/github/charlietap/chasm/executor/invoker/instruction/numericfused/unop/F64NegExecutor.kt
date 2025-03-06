package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64NegExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Neg,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = (-double).toRawBits()

    instruction.destination(result, stack)
}
