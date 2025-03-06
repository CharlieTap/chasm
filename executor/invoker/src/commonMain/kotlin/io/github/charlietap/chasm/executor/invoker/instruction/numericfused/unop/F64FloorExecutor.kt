package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.floor

internal inline fun F64FloorExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Floor,
) {
    val stack = context.vstack

    val double = Double.fromBits(instruction.operand(stack))
    val result = floor(double).toRawBits()

    instruction.destination(result, stack)
}
