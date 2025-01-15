package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.bind
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
    val (stack, store) = context
    val firstBlock = stack.popI32().bind() != 0

    if (firstBlock) {
        blockExecutor(store, stack, instruction.functionType, instruction.thenInstructions)
    } else {
        blockExecutor(store, stack, instruction.functionType, instruction.elseInstructions ?: emptyArray())
    }
}
