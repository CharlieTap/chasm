package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.truncate

internal inline fun F64TruncExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Trunc,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = truncate(double).toRawBits()

    instruction.destination(result, stack)
}
