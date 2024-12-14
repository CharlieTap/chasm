package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction

internal inline fun DataDropExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val dataAddress = frame.instance
        .dataAddress(instruction.dataIdx)
        .bind()
    val dataInstance = DataInstance(ubyteArrayOf(), true)
    store.data[dataAddress.address] = dataInstance
}
