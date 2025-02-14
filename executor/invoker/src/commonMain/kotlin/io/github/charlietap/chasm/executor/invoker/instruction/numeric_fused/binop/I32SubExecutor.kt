package io.github.charlietap.chasm.executor.invoker.instruction.numeric_fused.binop

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedNumericInstruction

internal inline fun I32SubExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I32Sub,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) - instruction.right(stack), stack)
}
