package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal typealias BreakExecutor = (ControlStack, Index.LabelIndex) -> Unit

internal inline fun BreakExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.Br,
) = BreakExecutor(
    controlStack = cstack,
    labelIndex = instruction.labelIndex,
)

internal inline fun BreakExecutor(
    controlStack: ControlStack,
    labelIndex: Index.LabelIndex,
) {
    val breakLabel = controlStack.peekNthLabel(labelIndex.index())

    val depths = breakLabel.depths
    controlStack.shrinkHandlers(depths.handlers)
    controlStack.shrinkInstructions(depths.instructions)
    controlStack.shrinkLabels(depths.labels)

    breakLabel.continuation?.let { continuation ->
        controlStack.push(continuation)
    }
}
