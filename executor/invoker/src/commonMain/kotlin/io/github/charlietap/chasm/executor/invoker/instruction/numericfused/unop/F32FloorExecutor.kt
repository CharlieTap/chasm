package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.floor

internal inline fun F32FloorExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Floor,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = floor(float).toRawBits().toLong()

    instruction.destination(result, stack)
}
