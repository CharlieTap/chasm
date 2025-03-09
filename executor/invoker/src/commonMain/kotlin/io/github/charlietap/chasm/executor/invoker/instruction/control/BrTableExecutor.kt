package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun BrTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
) = BrTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    breakExecutor = ::BreakExecutor,
)

internal inline fun BrTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrTable,
    crossinline breakExecutor: BreakExecutor,
) {
    val index = vstack.popI32()

    val label = if (index >= 0 && index < instruction.labelIndices.size) {
        instruction.labelIndices[index]
    } else {
        instruction.defaultLabelIndex
    }

    breakExecutor(cstack, vstack, label)
}
