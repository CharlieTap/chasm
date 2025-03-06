package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.unop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction
import kotlin.math.sqrt

internal inline fun F32SqrtExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Sqrt,
) {
    val stack = context.vstack

    val float = Float.fromBits(instruction.operand(stack).toInt())
    val result = sqrt(float).toRawBits().toLong()

    instruction.destination(result, stack)
}
