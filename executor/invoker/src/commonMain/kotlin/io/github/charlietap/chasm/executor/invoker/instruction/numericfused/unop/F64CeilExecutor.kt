package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.ceil

internal inline fun F64CeilExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Ceil,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = ceil(double).toRawBits()

    instruction.destination(result, stack)
}
