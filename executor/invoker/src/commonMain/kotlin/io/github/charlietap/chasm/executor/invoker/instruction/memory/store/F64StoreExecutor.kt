package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Store,
) = F64StoreExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    boundsChecker = ::PessimisticBoundsChecker,
    writer = ::F64Writer,
)

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F64Writer,
) {
    val memory = instruction.memory

    val valueToStore = vstack.popF64()
    val baseAddress = vstack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
}
