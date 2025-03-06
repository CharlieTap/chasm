package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32NegExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Neg,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = (-float).toRawBits().toLong()

    instruction.destination(result, stack)
}
