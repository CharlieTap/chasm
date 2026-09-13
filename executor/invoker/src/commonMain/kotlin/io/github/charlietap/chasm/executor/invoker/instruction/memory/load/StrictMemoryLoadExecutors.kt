package io.github.charlietap.chasm.executor.invoker.instruction.memory.load

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
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal inline fun MemorySizeExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.MemorySizeS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, instruction.memory.type.limits.min.toLong())
}

internal inline fun I32LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32LoadI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I32LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32LoadS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64LoadI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64LoadS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun F32LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32LoadI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueF32Load(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun F32LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F32LoadS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueF32Load(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun F64LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64LoadI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueF64Load(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun F64LoadExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.F64LoadS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueF64Load(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I32Load8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load8S(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I32Load8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load8S(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I32Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load8U(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I32Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load8US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load8U(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load16S(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I32Load16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load16S(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I32Load16UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load16U(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I32Load16UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I32Load16US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI32Load16U(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load8S(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load8SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load8S(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load8U(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load8UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load8US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load8U(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load16S(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load16SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load16S(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load16UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load16U(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load16UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load16US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load16U(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load32SI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load32S(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load32SExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load32SS,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load32S(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun I64Load32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load32UI,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load32U(instruction.memory, instruction.address, instruction.memArg.offset))
}

internal inline fun I64Load32UExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: MemoryInstruction.I64Load32US,
) {
    vstack.setFrameSlot(instruction.destinationSlot, valueI64Load32U(instruction.memory, vstack.getFrameSlot(instruction.addressSlot).toInt(), instruction.memArg.offset))
}

internal inline fun memoryLoadValue(
    memory: MemoryInstance,
    address: Int,
    offset: Int,
    bytes: Int,
    crossinline operation: (Int) -> Long,
): Long {
    if (address < 0 || offset < 0) {
        throw InvocationException(InvocationError.MemoryOperationOutOfBounds)
    }

    val effectiveAddress = address + offset
    return OptimisticBoundsChecker(effectiveAddress, bytes, memory.size) {
        operation(effectiveAddress)
    }
}
