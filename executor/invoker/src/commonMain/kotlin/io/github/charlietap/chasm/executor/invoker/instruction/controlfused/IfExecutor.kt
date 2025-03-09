package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.instruction.control.BlockExecutor
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun IfExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedControlInstruction.If,
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
    instruction: FusedControlInstruction.If,
    crossinline blockExecutor: BlockExecutor,
) {
    val value = instruction.operand(vstack)
    val branchIndex = ((value or -value) ushr 63).toInt()

    blockExecutor(store, cstack, vstack, instruction.params, instruction.results, instruction.instructions[branchIndex])
}
