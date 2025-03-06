package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.relop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun F64LeExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.F64Le,
) {
    val stack = context.vstack
    val left = Double.fromBits(instruction.left(stack))
    val right = Double.fromBits(instruction.right(stack))

    instruction.destination(if (left <= right) 1L else 0L, stack)
}
