@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias InstructionBlockExecutor = (Stack, Stack.Entry.Label, List<ExecutionInstruction>, List<ExecutionValue>, ExceptionHandler?) -> Result<Unit, InvocationError>

internal inline fun InstructionBlockExecutor(
    stack: Stack,
    label: Stack.Entry.Label,
    instructions: List<ExecutionInstruction>,
    params: List<ExecutionValue>,
    handler: ExceptionHandler?,
): Result<Unit, InvocationError> = binding {

    handler?.let {
        stack.push(Stack.Entry.Instruction(AdminInstruction.Handler(handler)))
    }
    stack.push(label)

    params.asReversed().forEach { value ->
        stack.push(Stack.Entry.Value(value))
    }

    instructions.asReversed().forEach { instruction ->
        stack.push(Stack.Entry.Instruction(instruction))
    }
}
