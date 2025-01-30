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
    val value = stack.pop()
    val branchIndex = ((value or -value) ushr 63).toInt()

    blockExecutor(store, context.cstack, stack, instruction.params, instruction.results, instruction.instructions[branchIndex])
}
