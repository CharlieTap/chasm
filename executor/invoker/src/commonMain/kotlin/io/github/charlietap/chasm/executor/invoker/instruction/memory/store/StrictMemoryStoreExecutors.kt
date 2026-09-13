package io.github.charlietap.chasm.executor.invoker.instruction.memory.store

import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
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
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32StoreIi,
) {
    valueI32Store(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32StoreIs,
) {
    valueI32Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32StoreSi,
) {
    valueI32Store(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32StoreSs,
) {
    valueI32Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64StoreIi,
) {
    valueI64Store(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64StoreIs,
) {
    valueI64Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64StoreSi,
) {
    valueI64Store(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64StoreSs,
) {
    valueI64Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32StoreIi,
) {
    valueF32Store(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32StoreIs,
) {
    valueF32Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32StoreSi,
) {
    valueF32Store(instruction.memory, instruction.address, instruction.memArg.offset, Float.fromBits(vstack.getFrameSlot(instruction.valueSlot).toInt()))
}

internal inline fun F32StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32StoreSs,
) {
    valueF32Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Float.fromBits(vstack.getFrameSlot(instruction.valueSlot).toInt()))
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64StoreIi,
) {
    valueF64Store(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64StoreIs,
) {
    valueF64Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64StoreSi,
) {
    valueF64Store(instruction.memory, instruction.address, instruction.memArg.offset, Double.fromBits(vstack.getFrameSlot(instruction.valueSlot)))
}

internal inline fun F64StoreExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64StoreSs,
) {
    valueF64Store(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, Double.fromBits(vstack.getFrameSlot(instruction.valueSlot)))
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8Ii,
) {
    valueI32Store8(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8Is,
) {
    valueI32Store8(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8Si,
) {
    valueI32Store8(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store8Ss,
) {
    valueI32Store8(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16Ii,
) {
    valueI32Store16(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16Is,
) {
    valueI32Store16(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16Si,
) {
    valueI32Store16(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I32Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Store16Ss,
) {
    valueI32Store16(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot).toInt())
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8Ii,
) {
    valueI64Store8(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8Is,
) {
    valueI64Store8(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8Si,
) {
    valueI64Store8(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store8Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store8Ss,
) {
    valueI64Store8(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16Ii,
) {
    valueI64Store16(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16Is,
) {
    valueI64Store16(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16Si,
) {
    valueI64Store16(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store16Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store16Ss,
) {
    valueI64Store16(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32Ii,
) {
    valueI64Store32(instruction.memory, instruction.address, instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32Is,
) {
    valueI64Store32(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.value)
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32Si,
) {
    valueI64Store32(instruction.memory, instruction.address, instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun I64Store32Executor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Store32Ss,
) {
    valueI64Store32(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, vstack.getFrameSlot(instruction.valueSlot))
}

internal inline fun memoryStoreValue(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline operation: (Int) -> Unit,
) {
    val effectiveAddress = address + offset
    OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }
}
