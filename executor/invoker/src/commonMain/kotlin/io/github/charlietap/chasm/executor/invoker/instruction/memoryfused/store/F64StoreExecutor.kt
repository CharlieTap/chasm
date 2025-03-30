package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.memory.BoundsChecker
import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

fun F64StoreExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Store,
): InstructionPointer =
    F64StoreExecutor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        boundsChecker = ::PessimisticBoundsChecker,
        writer = ::F64Writer,
    )

internal inline fun F64StoreExecutor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64Store,
    crossinline boundsChecker: BoundsChecker<Unit>,
    crossinline writer: F64Writer,
): InstructionPointer {
    val memory = instruction.memory

    val valueToStore = Double.fromBits(instruction.value(vstack))
    val baseAddress = instruction.address(vstack).toInt()
    val effectiveAddress = baseAddress + instruction.memArg.offset

    boundsChecker(effectiveAddress, Double.SIZE_BYTES, memory.size) {
        writer(memory.data, effectiveAddress, valueToStore)
    }

    return ip + 1
}
