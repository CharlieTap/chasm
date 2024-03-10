@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store

internal inline fun <T> StoreSizedNumberValueExecutorImpl(
    store: Store,
    stack: Stack,
    memArg: MemArg,
    sizeInBytes: Int,
    crossinline popper: StackValuePopper<T>,
    crossinline writer: SizedNumberValueWriter<T>,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrameOrError().bind()
    val memoryAddress = frame.state.module.memoryAddress(0).bind()
    val memory = store.memory(memoryAddress).bind()

    val valueToStore = popper(stack).bind()

    val baseAddress = stack.popI32().bind()

    val effectiveAddress = baseAddress + memArg.offset.toInt()

    writer(memory.data, valueToStore, effectiveAddress, sizeInBytes).bind()
}
