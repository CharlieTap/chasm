package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun I64Store8Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8,
): InstructionPointer =
    I64Store8Executor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::I64ToI8Writer,
    )

internal inline fun I64Store8Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: I64ToI8Writer,
): InstructionPointer {
    val memory = instruction.memory

    val valueToStore = instruction.value(vstack)
    val baseAddress = instruction.address(vstack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Byte.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }

    return ip + 1
}
