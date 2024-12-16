package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.forEachReversed
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Stack.Entry.InstructionTag
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekNthLabel
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
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

    val results = List(breakLabel.arity.value) {
        stack.popValue().bind()
    }

    repeat(labelIndex.index() + 1) {
        val label = stack.popLabel().bind()
        while (stack.valuesDepth() != label.stackValuesDepth) {
            stack.popValue().bind()
        }
        jumpToInstruction(stack).bind()
    }

    results.forEachReversed { value ->
        stack.push(value)
    }
    breakLabel.continuation.forEachReversed { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }
}

private fun jumpToInstruction(stack: Stack): Result<Unit, InvocationError> = binding {
    do {
        val instruction = stack.popInstruction().bind()
    } while (instruction.tag != InstructionTag.LABEL)
}
