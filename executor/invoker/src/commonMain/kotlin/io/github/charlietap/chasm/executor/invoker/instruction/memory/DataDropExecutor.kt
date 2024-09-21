@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance

internal inline fun DataDropExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, InvocationError> = binding {
    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val dataAddress = frame.state.module.dataAddress(instruction.dataIdx).bind()
    val dataInstance = DataInstance(ubyteArrayOf(), true)
    store.data[dataAddress.address] = dataInstance
}
