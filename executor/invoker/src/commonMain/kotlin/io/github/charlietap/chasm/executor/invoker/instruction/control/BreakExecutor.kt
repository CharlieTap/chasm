package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekNthLabel
import io.github.charlietap.chasm.executor.runtime.ext.popInstruction
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction

internal typealias BreakExecutor = (Stack, Index.LabelIndex) -> Result<Unit, InvocationError>

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

    results.asReversed().forEach { value ->
        stack.push(value)
    }
    breakLabel.continuation.asReversed().forEach { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }
}

private fun jumpToInstruction(stack: Stack): Result<Unit, InvocationError> = binding {
    do {
        val instruction = stack.popInstruction().bind()
    } while (instruction.instruction !is AdminInstruction.Label)
}
