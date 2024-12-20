package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.ext.memoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.executor.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory
import io.github.charlietap.chasm.executor.runtime.value.NumberValue

fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
): Result<Unit, InvocationError> =
    MemoryGrowExecutor(
        context = context,
        instruction = instruction,
        grower = ::LinearMemoryGrower,
    )

internal inline fun MemoryGrowExecutor(
    context: ExecutionContext,
    instruction: MemoryInstruction.MemoryGrow,
    crossinline grower: LinearMemoryGrower,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val memoryAddress = frame.instance
        .memoryAddress(instruction.memoryIndex)
        .bind()
    val memory = store.memory(memoryAddress).bind()

    val currentSizeInPages = memory.type.limits.min
        .toInt()
    val pagesToAdd = stack.popI32().bind()

    val max = memory.type.limits.max
        ?.toInt() ?: LinearMemory.MAX_PAGES
    if (memory.type.limits.min
            .toInt() + pagesToAdd > max
    ) {
        stack.push(Stack.Entry.Value(NumberValue.I32(-1)))
    } else {

        val newLimits = memory.type.limits.copy(
            min = memory.type.limits.min + pagesToAdd.toUInt(),
        )
        val newType = MemoryType(newLimits, memory.type.shared)
        val newMemory = grower(memory.data, pagesToAdd).bind()

        store.memories[memoryAddress.address] = MemoryInstance(newType, newMemory)
        stack.push(Stack.Entry.Value(NumberValue.I32(currentSizeInPages)))
    }
}
