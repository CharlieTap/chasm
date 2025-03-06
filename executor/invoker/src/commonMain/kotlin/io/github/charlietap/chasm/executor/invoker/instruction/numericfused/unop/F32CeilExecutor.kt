package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.ceil

internal inline fun F32CeilExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Ceil,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = ceil(float).toRawBits().toLong()

    instruction.destination(result, stack)
}
