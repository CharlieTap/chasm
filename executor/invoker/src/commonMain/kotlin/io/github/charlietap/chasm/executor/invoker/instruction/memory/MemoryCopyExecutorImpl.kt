@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutorImpl
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedSignedExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemoryCopyExecutorImpl(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    MemoryCopyExecutorImpl(
        store = store,
        stack = stack,
        i32UnsignedLoadExecutor = ::I32SizedUnsignedLoadExecutorImpl,
        i32StoreSizedSignedExecutor = ::I32StoreSizedSignedExecutorImpl,
    )

internal fun MemoryCopyExecutorImpl(
    store: Store,
    stack: Stack,
    i32UnsignedLoadExecutor: I32SizedUnsignedLoadExecutor,
    i32StoreSizedSignedExecutor: I32StoreSizedSignedExecutor,
): Result<Unit, InvocationError> = binding {

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val destinationOffset = stack.popI32().bind()

    if (bytesToCopy == 0) return@binding

    if (destinationOffset <= sourceOffset) {
        stack.push(Stack.Entry.Value(NumberValue.I32(destinationOffset)))
        stack.push(Stack.Entry.Value(NumberValue.I32(sourceOffset)))
        i32UnsignedLoadExecutor(store, stack, MemArg(0u, 0u), bytesToCopy).bind()
        i32StoreSizedSignedExecutor(store, stack, MemArg(0u, 0u), bytesToCopy).bind()
        stack.push(Stack.Entry.Value(NumberValue.I32(destinationOffset + 1)))
        stack.push(Stack.Entry.Value(NumberValue.I32(sourceOffset + 1)))
    } else {
        stack.push(Stack.Entry.Value(NumberValue.I32(destinationOffset + bytesToCopy + 1)))
        stack.push(Stack.Entry.Value(NumberValue.I32(sourceOffset + bytesToCopy - 1)))
        i32UnsignedLoadExecutor(store, stack, MemArg(0u, 0u), bytesToCopy).bind()
        i32StoreSizedSignedExecutor(store, stack, MemArg(0u, 0u), bytesToCopy).bind()
        stack.push(Stack.Entry.Value(NumberValue.I32(destinationOffset)))
        stack.push(Stack.Entry.Value(NumberValue.I32(sourceOffset)))
    }

    stack.push(Stack.Entry.Value(NumberValue.I32(bytesToCopy - 1)))

    MemoryCopyExecutorImpl(store, stack, i32UnsignedLoadExecutor, i32StoreSizedSignedExecutor).bind()
}
