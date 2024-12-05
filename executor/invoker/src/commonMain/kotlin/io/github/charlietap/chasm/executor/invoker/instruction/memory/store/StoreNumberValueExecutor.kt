package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias StackValuePopper<T> = Stack.() -> Result<T, InvocationError>
internal typealias NumberValueWriter<T> = (MemoryInstance, T, Int, Int) -> Result<Unit, InvocationError.MemoryOperationOutOfBounds>
internal typealias StoreNumberValueExecutor<T> = (
    Store,
    Stack,
    Index.MemoryIndex,
    MemArg,
    Int,
    StackValuePopper<T>,
    NumberValueWriter<T>,
) -> Result<Unit, InvocationError>

internal inline fun <T> StoreNumberValueExecutor(
    store: Store,
    stack: Stack,
    memoryIndex: Index.MemoryIndex,
    memArg: MemArg,
    valueSizeInBytes: Int,
    crossinline popper: StackValuePopper<T>,
    crossinline writer: NumberValueWriter<T>,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(memoryIndex).bind()
    val memory = store.memory(memoryAddress).bind()

    val valueToStore = popper(stack).bind()

    val baseAddress = stack.popI32().bind()
    val effectiveAddress = baseAddress + memArg.offset.toInt()

    writer(memory, valueToStore, effectiveAddress, valueSizeInBytes).bind()
}
