package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemorySizeExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemorySize,
): Result<Unit, InvocationError> = binding {
    val stack = context.stack
    val currentSizeInPages = instruction.memory.type.limits.min
        .toInt()

    stack.push(Stack.Entry.Value(NumberValue.I32(currentSizeInPages)))
}
