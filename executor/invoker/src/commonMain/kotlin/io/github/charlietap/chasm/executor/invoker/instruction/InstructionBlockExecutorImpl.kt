package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.invoker.flow.BreakException
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popLabel
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal fun InstructionBlockExecutorImpl(
    store: Store,
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<Instruction>,
    params: List<ExecutionValue>,
): Result<Unit, InvocationError> =
    InstructionBlockExecutorImpl(
        store = store,
        stack = stack,
        label = label,
        instructions = instructions,
        params = params,
        instructionExecutor = ::InstructionExecutorImpl,
    )

internal fun InstructionBlockExecutorImpl(
    store: Store,
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<Instruction>,
    params: List<ExecutionValue>,
    instructionExecutor: InstructionExecutor,
): Result<Unit, InvocationError> = binding {

    stack.push(label)

    val valuesDepth = stack.valuesDepth()

    params.forEach { value ->
        stack.push(Stack.Entry.Value(value))
    }

    try {
        instructions.forEach { instruction ->
            instructionExecutor(instruction, store, stack).bind()
        }
        stack.popLabel().bind()
    } catch (exception: BreakException) {

        while (stack.valuesDepth() > valuesDepth) {
            stack.popValue().bind()
        }

        stack.popLabel().bind()

        if (exception.labelsToBreak != 0) {
            throw exception.copy(labelsToBreak = exception.labelsToBreak - 1)
        } else {
            exception.results.forEach { value ->
                stack.push(Stack.Entry.Value(value))
            }
            exception.continuation.forEach { instruction ->
                instructionExecutor(instruction, store, stack).bind()
            }
        }
    }
}
