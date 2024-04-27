@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal inline fun InstructionBlockExecutorImpl(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<ExecutionInstruction>,
    params: List<ExecutionValue>,
): Result<Unit, InvocationError> = binding {

    stack.push(label)

    params.asReversed().forEach { value ->
        stack.push(Stack.Entry.Value(value))
    }

    instructions.asReversed().forEach { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }
}
