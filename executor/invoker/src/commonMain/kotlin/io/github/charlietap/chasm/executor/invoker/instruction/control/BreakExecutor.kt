package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekNthLabel
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal typealias BreakExecutor = (Stack, Index.LabelIndex) -> Unit

internal inline fun BreakExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Br,
) = BreakExecutor(
        stack = context.stack,
        labelIndex = instruction.labelIndex,
    )

internal inline fun BreakExecutor(
    stack: Stack,
    labelIndex: Index.LabelIndex,
) {
    val breakLabel = stack.peekNthLabel(labelIndex.index()).bind()

    val depths = breakLabel.depths
    stack.shrinkInstructions(0, depths.instructions)
    stack.shrinkLabels(0, depths.labels)
    stack.shrinkValues(breakLabel.arity, depths.values)

    breakLabel.continuation?.let { continuation ->
        stack.push(continuation)
    }
}
