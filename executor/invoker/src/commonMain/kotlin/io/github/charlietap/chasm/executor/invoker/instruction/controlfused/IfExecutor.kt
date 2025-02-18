package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.control.BlockExecutor
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedControlInstruction

internal fun IfExecutor(
    context: ExecutionContext,
    instruction: FusedControlInstruction.If,
) = IfExecutor(
    context = context,
    instruction = instruction,
    blockExecutor = ::BlockExecutor,
)

internal inline fun IfExecutor(
    context: ExecutionContext,
    instruction: FusedControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
) {
    val stack = context.vstack
    val store = context.store
    val value = instruction.operand(stack)
    val branchIndex = ((value or -value) ushr 63).toInt()

    blockExecutor(store, context.cstack, stack, instruction.params, instruction.results, instruction.instructions[branchIndex])
}
