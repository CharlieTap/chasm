package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun F64LoadExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Load,
): InstructionPointer =
    F64LoadExecutor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::OptimisticBoundsChecker,
        reader = ::F64Reader,
    )

internal inline fun F64LoadExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Load,
    crossinline boundsChecker: BoundsChecker<Double>,
    crossinline reader: F64Reader,
): InstructionPointer {
    val memory = instruction.memory

    val baseAddress = instruction.address(vstack).toInt()
    val offset = instruction.memArg.offset
    val effectiveAddress = baseAddress + offset

    if (baseAddress < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val result = boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        reader(memory.data, effectiveAddress)
    }

    instruction.destination(result.toLong(), vstack)

    return ip + 1
}
