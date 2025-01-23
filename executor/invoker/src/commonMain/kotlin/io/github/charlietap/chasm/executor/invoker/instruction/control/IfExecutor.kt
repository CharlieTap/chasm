package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popI32
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
    val firstBlock = stack.popI32() != 0

    if (firstBlock) {
        blockExecutor(store, context.cstack, stack, instruction.functionType, instruction.thenInstructions)
    } else {
        blockExecutor(store, context.cstack, stack, instruction.functionType, instruction.elseInstructions ?: emptyArray())
    }
}
