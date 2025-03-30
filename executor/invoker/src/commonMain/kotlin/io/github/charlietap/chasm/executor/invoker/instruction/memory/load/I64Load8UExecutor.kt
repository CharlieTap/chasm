package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.I648UReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I64Load8UExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8U,
): InstructionPointer =
    I64Load8UExecutor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::I648UReader,
    )

internal inline fun I64Load8UExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8U,
    crossinline boundsChecker: BoundsChecker<Long>,
    crossinline reader: I648UReader,
): InstructionPointer {
    val memory = instruction.memory

    val baseAddress = vstack.popI32()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, 1, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    vstack.pushI64(result)
    return ip + 1
}
