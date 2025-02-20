package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32XOrExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Xor,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) xor instruction.right(stack), stack)
}
