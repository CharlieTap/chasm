package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F32GeExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F32Ge,
) {
    val stack = context.vstack
    val left = Float.fromBits(instruction.left(stack).toInt())
    val right = Float.fromBits(instruction.right(stack).toInt())

    instruction.destination(if (left >= right) 1L else 0L, stack)
}
