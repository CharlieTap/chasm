package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store

import io.github.charlietap.chasm.memory.PessimisticBoundsChecker
import io.github.charlietap.chasm.memory.write.F32Writer
import io.github.charlietap.chasm.memory.write.F64Writer
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64ToI16Writer
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.memory.write.I64ToI8Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32StoreIi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32StoreIs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32StoreSi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32StoreSs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I32Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64StoreIi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64StoreIs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64StoreSi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64StoreSs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Long.SIZE_BYTES) { effectiveAddress ->
    I64Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32StoreIi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32StoreIs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32StoreSi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Writer(instruction.memory.data, effectiveAddress, Float.fromBits(vstack.getFrameSlot(instruction.valueSlot).toInt()))
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F32StoreSs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Float.SIZE_BYTES) { effectiveAddress ->
    F32Writer(instruction.memory.data, effectiveAddress, Float.fromBits(vstack.getFrameSlot(instruction.valueSlot).toInt()))
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64StoreIi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64StoreIs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64StoreSi,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Writer(instruction.memory.data, effectiveAddress, Double.fromBits(vstack.getFrameSlot(instruction.valueSlot)))
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.F64StoreSs,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Double.SIZE_BYTES) { effectiveAddress ->
    F64Writer(instruction.memory.data, effectiveAddress, Double.fromBits(vstack.getFrameSlot(instruction.valueSlot)))
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store8Ii,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I32ToI8Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store8Is,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I32ToI8Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store8Si,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I32ToI8Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store8Ss,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I32ToI8Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16Ii,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I32ToI16Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16Is,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I32ToI16Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16Si,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I32ToI16Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I32Store16Ss,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I32ToI16Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8Ii,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I64ToI8Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8Is,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I64ToI8Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8Si,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I64ToI8Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store8Ss,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Byte.SIZE_BYTES) { effectiveAddress ->
    I64ToI8Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store16Ii,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I64ToI16Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store16Is,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I64ToI16Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store16Si,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I64ToI16Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store16Ss,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Short.SIZE_BYTES) { effectiveAddress ->
    I64ToI16Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32Ii,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I64ToI32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32Is,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I64ToI32Writer(instruction.memory.data, effectiveAddress, instruction.value)
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32Si,
) = executeMemoryStore(instruction.memory, instruction.address, instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I64ToI32Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedMemoryInstruction.I64Store32Ss,
) = executeMemoryStore(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Int.SIZE_BYTES) { effectiveAddress ->
    I64ToI32Writer(instruction.memory.data, effectiveAddress, vstack.getFrameSlot(instruction.valueSlot))
}

private inline fun executeMemoryStore(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline operation: (Int) -> Unit,
) {
    val effectiveAddress = address + offset
    PessimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }
}
