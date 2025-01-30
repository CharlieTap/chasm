package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun IfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.If,
) = IfExecutor(
    context = context,
    instruction = instruction,
    blockExecutor = ::BlockExecutor,
)

internal inline fun IfExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
) {
    val stack = context.vstack
    val store = context.store
    val firstBlock = stack.pop() != 0L

    if (firstBlock) {
        blockExecutor(store, context.cstack, stack, instruction.params, instruction.results, instruction.thenInstructions)
    } else {
        blockExecutor(store, context.cstack, stack, instruction.params, instruction.results, instruction.elseInstructions)
    }
}
