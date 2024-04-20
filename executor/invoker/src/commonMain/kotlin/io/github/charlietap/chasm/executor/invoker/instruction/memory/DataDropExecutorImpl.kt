@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instance.DataInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun DataDropExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.DataDrop,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrame().bind()
    val dataAddress = frame.state.module.dataAddress(instruction.dataIdx.index()).bind()
    val dataInstance = DataInstance(ubyteArrayOf(), true)
    store.data[dataAddress.address] = dataInstance
}
