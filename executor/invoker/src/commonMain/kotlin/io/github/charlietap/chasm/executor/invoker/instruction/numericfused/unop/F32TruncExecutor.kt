package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.truncate

internal inline fun F32TruncExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Trunc,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = truncate(float).toRawBits().toLong()

    instruction.destination(result, stack)
}
