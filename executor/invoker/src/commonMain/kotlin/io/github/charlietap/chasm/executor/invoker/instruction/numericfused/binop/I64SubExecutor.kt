package io.github.charlietap.chasm.executor.invoker.instruction.numericfused.binop

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedNumericInstruction

internal inline fun I64SubExecutor(
    context: ExecutionContext,
    instruction: FusedNumericInstruction.I64Sub,
) {
    val stack = context.vstack
    instruction.destination(instruction.left(stack) - instruction.right(stack), stack)
}
