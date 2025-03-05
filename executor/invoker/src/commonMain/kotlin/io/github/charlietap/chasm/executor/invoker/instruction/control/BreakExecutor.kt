package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal typealias BreakExecutor = (ControlStack, ValueStack, Index.LabelIndex) -> Unit

internal inline fun BreakExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Br,
) = BreakExecutor(
    controlStack = context.cstack,
    valueStack = context.vstack,
    labelIndex = instruction.labelIndex,
)

internal inline fun BreakExecutor(
    controlStack: ControlStack,
    valueStack: ValueStack,
    labelIndex: Index.LabelIndex,
) {
    val breakLabel = controlStack.peekNthLabel(labelIndex.index())

    val depths = breakLabel.depths
    controlStack.shrinkHandlers(depths.handlers)
    controlStack.shrinkInstructions(depths.instructions)
    controlStack.shrinkLabels(depths.labels)
    valueStack.shrink(breakLabel.arity, depths.values)

    breakLabel.continuation?.let { continuation ->
        controlStack.push(continuation)
    }
}
