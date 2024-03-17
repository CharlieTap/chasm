@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemoryFillExecutorImpl(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> =
    MemoryFillExecutorImpl(
        store = store,
        stack = stack,
        storeSizedSignedExecutor = ::I32StoreSizedExecutorImpl,
    )

internal fun MemoryFillExecutorImpl(
    store: Store,
    stack: Stack,
    storeSizedSignedExecutor: I32StoreSizedExecutor,
): Result<Unit, InvocationError> = binding {

    val bytesToFill = stack.popI32().bind()
    val fillValue = stack.popI32().bind()
    val offset = stack.popI32().bind()

    if (bytesToFill == 0) return@binding

    stack.push(Stack.Entry.Value(NumberValue.I32(offset)))
    stack.push(Stack.Entry.Value(NumberValue.I32(fillValue)))

    storeSizedSignedExecutor(store, stack, MemArg(0u, 0u), 1).bind()

    stack.push(Stack.Entry.Value(NumberValue.I32(offset + 1)))
    stack.push(Stack.Entry.Value(NumberValue.I32(fillValue)))
    stack.push(Stack.Entry.Value(NumberValue.I32(bytesToFill - 1)))

    MemoryFillExecutorImpl(store, stack, storeSizedSignedExecutor).bind()
}
