package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.forEachReversed
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekNthLabel
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal typealias BreakExecutor = (Stack, Index.LabelIndex) -> Result<Unit, InvocationError>

internal inline fun BreakExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Br,
): Result<Unit, InvocationError> =
    BreakExecutor(
        stack = context.stack,
        labelIndex = instruction.labelIndex,
    )

internal inline fun BreakExecutor(
    stack: Stack,
    labelIndex: Index.LabelIndex,
): Result<Unit, InvocationError> = binding {

    val breakLabel = stack.peekNthLabel(labelIndex.index()).bind()

    stack.shrinkInstructions(0, breakLabel.stackInstructionsDepth)
    stack.shrinkLabels(0, breakLabel.stackLabelsDepth)
    stack.shrinkValues(breakLabel.arity, breakLabel.stackValuesDepth)

    breakLabel.continuation.forEachReversed { instruction ->
        stack.push(instruction)
    }
}
