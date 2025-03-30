package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun I64StoreExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store,
): InstructionPointer =
    I64StoreExecutor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64Writer,
    )

internal inline fun I64StoreExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64Writer,
): InstructionPointer {
    val memory = instruction.memory

    val valueToStore = vstack.popI64()
    val baseAddress = vstack.popI32()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Long.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }
    return ip + 1
}
