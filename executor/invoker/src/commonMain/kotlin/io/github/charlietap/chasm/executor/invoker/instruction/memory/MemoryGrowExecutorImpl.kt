@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemoryGrowExecutorImpl(
    store: Store,
    stack: Stack,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrameOrError().bind()
    val memoryAddress = frame.state.module.memoryAddress(0).bind()
    val memory = store.memory(memoryAddress).bind()

    val sizeInPages = memory.data.min.amount

    val proposedSizeInPages = stack.popI32().bind()

    val result = memory.data.grow(LinearMemory.Pages(proposedSizeInPages))

    result.fold({ newMemory ->
        store.memories[memoryAddress.address] = memory.copy(
            type = MemoryType(memory.type.limits.copy(min = proposedSizeInPages.toUInt())),
            data = newMemory,
        )
        stack.push(Stack.Entry.Value(NumberValue.I32(sizeInPages)))
    }, {
        stack.push(Stack.Entry.Value(NumberValue.I32(-1)))
    })
}
