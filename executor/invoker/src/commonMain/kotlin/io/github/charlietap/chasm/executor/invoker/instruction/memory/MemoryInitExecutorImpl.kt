@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.ext.peekFrameOrError
import io.github.charlietap.chasm.executor.invoker.ext.popI32
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedSignedExecutorImpl
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal inline fun MemoryInitExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.MemoryInit,
): Result<Unit, InvocationError> =
    MemoryInitExecutorImpl(
        store = store,
        stack = stack,
        instruction = instruction,
        i32StoreSizedSignedExecutor = ::I32StoreSizedSignedExecutorImpl,
    )

internal fun MemoryInitExecutorImpl(
    store: Store,
    stack: Stack,
    instruction: MemoryInstruction.MemoryInit,
    i32StoreSizedSignedExecutor: I32StoreSizedSignedExecutor,
): Result<Unit, InvocationError> = binding {
    val frame = stack.peekFrameOrError().bind()
    val memoryAddress = frame.state.module.memoryAddress(0).bind()
    val memory = store.memory(memoryAddress).bind()

    val dataAddress = frame.state.module.dataAddress(instruction.dataIdx.index()).bind()
    val data = store.data(dataAddress).bind()

    val bytesToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val dataSegmentIndex = stack.popI32().bind()

    if ((sourceOffset + bytesToCopy) > data.bytes.size || (dataSegmentIndex + bytesToCopy) > memory.data.size()) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (bytesToCopy == 0) return@binding

    val byte = data.bytes[sourceOffset]

    stack.push(Stack.Entry.Value(NumberValue.I32(dataSegmentIndex)))
    stack.push(Stack.Entry.Value(NumberValue.I32(byte.toInt())))

    i32StoreSizedSignedExecutor(store, stack, MemArg(0u, 0u), 1).bind()

    stack.push(Stack.Entry.Value(NumberValue.I32(dataSegmentIndex + 1)))
    stack.push(Stack.Entry.Value(NumberValue.I32(sourceOffset + 1)))
    stack.push(Stack.Entry.Value(NumberValue.I32(bytesToCopy - 1)))

    MemoryInitExecutorImpl(store, stack, instruction, i32StoreSizedSignedExecutor).bind()
}
