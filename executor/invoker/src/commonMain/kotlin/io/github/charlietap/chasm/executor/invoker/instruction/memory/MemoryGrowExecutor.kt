package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.memory.grow.MemoryGrowerImpl
import io.github.charlietap.chasm.executor.memory.grow.MemoryInstanceGrower
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

internal fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
): Result<Unit, InvocationError> =
    MemoryGrowExecutor(
        context = context,
        instruction = instruction,
        memoryInstanceGrower = ::MemoryGrowerImpl,
    )

internal inline fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline memoryInstanceGrower: MemoryInstanceGrower,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.state.module.memoryAddress(instruction.memoryIndex).bind()
    val memory = store.memory(memoryAddress).bind()

    val sizeInPages = memory.data.min.amount

    val pagesToAdd = stack.popI32().bind()

    val result = memoryInstanceGrower(memory, pagesToAdd)

    result.fold({ newMemory ->
        store.memories[memoryAddress.address] = newMemory
        stack.push(Stack.Entry.Value(NumberValue.I32(sizeInPages)))
    }, {
        stack.push(Stack.Entry.Value(NumberValue.I32(-1)))
    })
}
