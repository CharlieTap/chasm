package io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load

import io.github.charlietap.chasm.memory.OptimisticBoundsChecker
import io.github.charlietap.chasm.memory.read.F32Reader
import io.github.charlietap.chasm.memory.read.F64Reader
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328SReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I6416SReader
import io.github.charlietap.chasm.memory.read.I6416UReader
import io.github.charlietap.chasm.memory.read.I6432SReader
import io.github.charlietap.chasm.memory.read.I6432UReader
import io.github.charlietap.chasm.memory.read.I648SReader
import io.github.charlietap.chasm.memory.read.I648UReader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal inline fun MemorySizeExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.MemorySizeS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.memory.type.limits.min.toLong())
}

internal inline fun I32LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32LoadI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I32Reader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32LoadS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I32Reader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I64LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64LoadI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Long.SIZE_BYTES) { effectiveAddress ->
    I64Reader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64LoadS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Long.SIZE_BYTES) { effectiveAddress ->
    I64Reader(instruction.memory.data, effectiveAddress)
}

internal inline fun F32LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.F32LoadI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Float.SIZE_BYTES) { effectiveAddress ->
    F32Reader(instruction.memory.data, effectiveAddress).toRawBits().toLong()
}

internal inline fun F32LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.F32LoadS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Float.SIZE_BYTES) { effectiveAddress ->
    F32Reader(instruction.memory.data, effectiveAddress).toRawBits().toLong()
}

internal inline fun F64LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.F64LoadI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Double.SIZE_BYTES) { effectiveAddress ->
    F64Reader(instruction.memory.data, effectiveAddress).toRawBits()
}

internal inline fun F64LoadExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.F64LoadS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Double.SIZE_BYTES) { effectiveAddress ->
    F64Reader(instruction.memory.data, effectiveAddress).toRawBits()
}

internal inline fun I32Load8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load8SI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I328SReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load8SS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I328SReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load8UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load8UI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I328UReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load8UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load8US,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I328UReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load16SI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I3216SReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load16SS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I3216SReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load16UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load16UI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I3216UReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I32Load16UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I32Load16US,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I3216UReader(instruction.memory.data, effectiveAddress).toLong()
}

internal inline fun I64Load8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load8SI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I648SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load8SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load8SS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I648SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load8UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load8UI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I648UReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load8UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load8US,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Byte.SIZE_BYTES) { effectiveAddress ->
    I648UReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load16SI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I6416SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load16SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load16SS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I6416SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load16UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load16UI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I6416UReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load16UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load16US,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Short.SIZE_BYTES) { effectiveAddress ->
    I6416UReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load32SI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I6432SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load32SExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load32SS,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I6432SReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load32UI,
) = executeMemoryLoad(vstack, instruction.memory, instruction.address, instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I6432UReader(instruction.memory.data, effectiveAddress)
}

internal inline fun I64Load32UExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: MemorySuperInstruction.I64Load32US,
) = executeMemoryLoad(vstack, instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset, instruction.destinationSlot, Int.SIZE_BYTES) { effectiveAddress ->
    I6432UReader(instruction.memory.data, effectiveAddress)
}

private inline fun executeMemoryLoad(
    vstack: ValueStack,
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    destinationSlot: Int,
    bytes: Int,
    crossinline operation: (Int) -> Long,
) {
    if (address < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val effectiveAddress = address + offset
    val result = OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }

    vstack.setFrameSlot(destinationSlot, result)
}
