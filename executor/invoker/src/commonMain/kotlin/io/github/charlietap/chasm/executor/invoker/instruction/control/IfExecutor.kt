package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.If,
) = IfExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    blockExecutor = ::BlockExecutor,
)

internal inline fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
) {
    val value = vstack.pop()
    val branchIndex = ((value or -value) ushr 63).toInt()

    blockExecutor(store, cstack, vstack, instruction.params, instruction.results, instruction.instructions[branchIndex])
}
