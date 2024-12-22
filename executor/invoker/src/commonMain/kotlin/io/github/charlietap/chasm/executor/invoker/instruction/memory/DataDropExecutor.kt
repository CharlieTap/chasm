package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun DataDropExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, InvocationError> = binding {
    instruction.data.bytes = DataInstance.EMPTY
}
