@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemorySizeExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.MemorySize,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(instruction.memoryIndex).bind()
    val memory = store.memory(memoryAddress).bind()

    val sizeInPages = memory.data.min.amount

    stack.push(Stack.Entry.Value(NumberValue.I32(sizeInPages)))
}
