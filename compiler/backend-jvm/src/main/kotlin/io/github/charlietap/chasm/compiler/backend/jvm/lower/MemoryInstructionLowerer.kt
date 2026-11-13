package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_LOAD
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_LOAD_16_S
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_LOAD_8_S
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_STORE
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_STORE_16
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I32_STORE_8
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I64_LOAD
import io.github.charlietap.chasm.compiler.backend.abi.BUFFER_I64_STORE
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_GET_BYTE
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_GET_INT
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_GET_LONG
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_GET_SHORT
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_PUT_BYTE
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_PUT_INT
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_PUT_LONG
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_PUT_SHORT
import io.github.charlietap.chasm.compiler.backend.abi.BYTE_BUFFER_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_ACCESS_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_BYTE_ARRAY_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_BYTE_BUFFER_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_COPY
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_FILL
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_SIZE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_GET_BYTE
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_GET_INT
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_GET_LONG
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_PUT_BYTE
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_PUT_INT
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_PUT_LONG
import io.github.charlietap.chasm.compiler.backend.abi.memoryAccess
import io.github.charlietap.chasm.compiler.backend.analysis.form.BulkMemory
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryAccess
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.form.bulk
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.runtime.diagnostic.FailedMemoryAccess
import io.github.charlietap.chasm.type.NumberType
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

internal enum class InlineMemory {
    BYTES,
    BUFFER,
    UNSAFE,
}

internal fun lowerMemoryInstruction(
    instruction: MemoryInstruction,
    context: LowerContext,
) {
    val access = instruction.access()
    if (access != null) {
        return if (context.locals.inlineMemory != null && access.memory.index == 0) context.inlineAccess(access) else context.memoryAccess(access)
    }
    when (val bulk = instruction.bulk()) {
        is BulkMemory.Copy -> return context.memoryCopy(bulk)
        is BulkMemory.Fill -> return context.memoryFill(bulk)
        null -> Unit
    }
    val call = instruction.helperCall() ?: unsupported(instruction)
    context.lowerHelperCall(call)
}

internal fun LowerContext.refreshMemory() {
    if (locals.inlineMemory == null) return
    pushResource(Resource.Memory(0))
    emit.insn(Opcodes.DUP)
    if (locals.inlineMemory == InlineMemory.BUFFER) {
        emit.getField(MEMORY_INSTANCE, MEMORY_BYTE_BUFFER_FIELD, OBJECT_TYPE)
        emit.type(Opcodes.CHECKCAST, BYTE_BUFFER_TYPE)
        emit.store(Opcodes.ASTORE, locals.memoryBuffer)
    } else {
        emit.getField(MEMORY_INSTANCE, MEMORY_BYTE_ARRAY_FIELD, "[B")
        emit.store(Opcodes.ASTORE, locals.memoryBytes)
    }
    emit.invokeVirtual(MEMORY_SIZE)
    emit.store(Opcodes.ISTORE, locals.memorySize)
}

internal fun LowerContext.initializeMemory() {
    if (locals.inlineMemory == InlineMemory.UNSAFE) {
        pushResource(Resource.Unsafe)
        emit.store(Opcodes.ASTORE, locals.unsafe)
        pushResource(Resource.ByteArrayBase)
        emit.store(Opcodes.LSTORE, locals.byteArrayBase)
    }
    refreshMemory()
}

private fun LowerContext.memoryAccess(
    access: MemoryAccess,
) {
    val operation = access.operation
    val type = operation.type
    pushResource(Resource.Memory(access.memory.index))
    pushInt(access.address)
    emit.int(access.offset)
    val value = access.value
    if (value == null) {
        emit.invokeStatic(memoryAccess(operation.helperName, value = null, result = descriptor(type)))
        store(type, access.destination)
    } else {
        push(type, value)
        emit.invokeStatic(memoryAccess(operation.helperName, value = descriptor(type), result = null))
    }
}

private fun LowerContext.inlineAccess(
    access: MemoryAccess,
) {

    val memory = checkNotNull(locals.inlineMemory)
    val boundsCheckedByHelper = memory == InlineMemory.BUFFER && !module.options.stacktraces
    val width = access.operation.width
    val offset = access.offset.toLong() and 0xFFFF_FFFFL
    val reach = offset + width
    val address = access.address
    if (reach > Int.MAX_VALUE) {
        accessOutOfBounds(access) { pushUnsignedAddress(address, offset) }
        return
    }

    if (address is Operand.Immediate) {
        val effective = (address.bits and 0xFFFF_FFFFL) + offset
        if (effective + width > Int.MAX_VALUE) {
            accessOutOfBounds(access) { emit.long(effective) }
            return
        }
        if (effective + width > declarations.memoryMinimumBytes(access.memory)) {
            val outOfBounds = outOfBounds(access) { emit.long(effective) }
            emit.int(effective.toInt())
            emit.load(Opcodes.ILOAD, locals.memorySize)
            emit.int(width)
            emit.insn(Opcodes.ISUB)
            emit.jump(Opcodes.IF_ICMPGT, outOfBounds)
        }
        emit.int(effective.toInt())
        emit.store(Opcodes.ISTORE, locals.scratchInt)
    } else if (checkedAccesses[position]) {
        pushInt(address)
        if (access.offset != 0) {
            emit.int(access.offset)
            emit.insn(Opcodes.IADD)
        }
        emit.store(Opcodes.ISTORE, locals.scratchInt)
    } else if (boundsCheckedByHelper) {
        bufferMemoryAccess(access)
        return
    } else {
        val outOfBounds = outOfBounds(access) { pushUnsignedScratch(offset) }
        pushInt(address)
        emit.insn(Opcodes.DUP)
        emit.store(Opcodes.ISTORE, locals.scratchInt)
        emit.jump(Opcodes.IFLT, outOfBounds)
        emit.load(Opcodes.ILOAD, locals.scratchInt)
        emit.load(Opcodes.ILOAD, locals.memorySize)
        emit.int(reach.toInt())
        emit.insn(Opcodes.ISUB)
        emit.jump(Opcodes.IF_ICMPGT, outOfBounds)
        if (access.offset != 0) {
            emit.load(Opcodes.ILOAD, locals.scratchInt)
            emit.int(access.offset)
            emit.insn(Opcodes.IADD)
            emit.store(Opcodes.ISTORE, locals.scratchInt)
        }
    }

    val value = access.value
    when {
        value == null -> inlineLoad(access.operation, access.destination)
        memory == InlineMemory.BUFFER -> bufferStore(access.operation, value)
        else -> inlineStore(access.operation, value)
    }
}

private fun LowerContext.bufferMemoryAccess(access: MemoryAccess) {

    val operation = access.operation
    emit.load(Opcodes.ALOAD, locals.memoryBuffer)
    emit.load(Opcodes.ILOAD, locals.memorySize)
    pushInt(access.address)
    emit.int(access.offset)

    val value = access.value
    if (value == null) {
        when (operation.width) {
            1 -> emit.invokeStatic(BUFFER_I32_LOAD_8_S)
            2 -> {
                emit.invokeStatic(BUFFER_I32_LOAD_16_S)
                if (!operation.signed) {
                    mask(0xFFFF)
                }
            }
            4 -> emit.invokeStatic(BUFFER_I32_LOAD)
            else -> emit.invokeStatic(BUFFER_I64_LOAD)
        }
        extendLoaded(operation)
        store(operation.type, access.destination)
        return
    }

    pushStored(operation, value)
    emit.invokeStatic(
        when (operation.width) {
            1 -> BUFFER_I32_STORE_8
            2 -> BUFFER_I32_STORE_16
            4 -> BUFFER_I32_STORE
            else -> BUFFER_I64_STORE
        },
    )
}

private fun LowerContext.inlineLoad(
    operation: MemoryOperation,
    destination: Int,
) {

    if (locals.inlineMemory == InlineMemory.BUFFER) {
        bufferGet(operation)
    } else {
        when (operation.width) {
            1 -> loadByte(0)
            2 -> loadShort(operation.signed)
            4 -> loadInt()
            else -> loadLong()
        }
    }

    extendLoaded(operation)
    store(operation.type, destination)
}

private fun LowerContext.bufferGet(operation: MemoryOperation) {

    emit.load(Opcodes.ALOAD, locals.memoryBuffer)
    emit.load(Opcodes.ILOAD, locals.scratchInt)

    when (operation.width) {
        1 -> emit.invokeVirtual(BYTE_BUFFER_GET_BYTE)
        2 -> {
            emit.invokeVirtual(BYTE_BUFFER_GET_SHORT)
            if (!operation.signed) {
                mask(0xFFFF)
            }
        }
        4 -> emit.invokeVirtual(BYTE_BUFFER_GET_INT)
        else -> emit.invokeVirtual(BYTE_BUFFER_GET_LONG)
    }
}

private fun LowerContext.extendLoaded(operation: MemoryOperation) {

    val wide = operation.type == NumberType.I64 || operation.type == NumberType.F64

    when (operation.width) {
        1 -> {
            if (!operation.signed) {
                mask(0xFF)
            }
            if (wide) {
                emit.insn(Opcodes.I2L)
            }
        }
        2 -> {
            if (wide) {
                emit.insn(Opcodes.I2L)
            }
        }
        4 -> {
            when {
                operation.type == NumberType.F32 -> emit.invokeStatic(INT_BITS_TO_FLOAT)
                operation.signed -> emit.insn(Opcodes.I2L)
                wide -> {
                    emit.insn(Opcodes.I2L)
                    emit.long(0xFFFF_FFFFL)
                    emit.insn(Opcodes.LAND)
                }
            }
        }
        else -> {
            if (operation.type == NumberType.F64) {
                emit.invokeStatic(LONG_BITS_TO_DOUBLE)
            }
        }
    }
}

private fun LowerContext.pushStored(
    operation: MemoryOperation,
    value: Operand,
) {
    val type = operation.type
    when (operation.width) {
        1, 2, 4 -> pushNarrow(type, value)
        else -> {
            if (type == NumberType.F64) {
                pushDouble(value)
                emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
            } else {
                pushLong(value)
            }
        }
    }
}

private fun LowerContext.bufferStore(
    operation: MemoryOperation,
    value: Operand,
) {

    emit.load(Opcodes.ALOAD, locals.memoryBuffer)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    pushStored(operation, value)

    when (operation.width) {
        1 -> {
            emit.insn(Opcodes.I2B)
            emit.invokeVirtual(BYTE_BUFFER_PUT_BYTE)
        }
        2 -> {
            emit.insn(Opcodes.I2S)
            emit.invokeVirtual(BYTE_BUFFER_PUT_SHORT)
        }
        4 -> emit.invokeVirtual(BYTE_BUFFER_PUT_INT)
        else -> emit.invokeVirtual(BYTE_BUFFER_PUT_LONG)
    }

    emit.insn(Opcodes.POP)
}

private fun LowerContext.outOfBounds(
    access: MemoryAccess,
    pushAddress: LowerContext.() -> Unit,
): Label {

    if (!module.options.stacktraces) {
        return trapLabel(MEMORY_OUT_OF_BOUNDS)
    }

    val origin = position
    return emit.label().also { label ->
        outOfLine += {
            emit.place(label)
            markSite(origin, label)
            trapAccess(access, pushAddress)
        }
    }
}

private fun LowerContext.accessOutOfBounds(
    access: MemoryAccess,
    pushAddress: LowerContext.() -> Unit,
) = if (module.options.stacktraces) trapAccess(access, pushAddress) else trap(MEMORY_OUT_OF_BOUNDS)

private fun LowerContext.trapAccess(
    access: MemoryAccess,
    pushAddress: LowerContext.() -> Unit,
) {
    pushResource(Resource.Memory(access.memory.index))
    emit.int(if (access.value == null) FailedMemoryAccess.Kind.READ.ordinal else FailedMemoryAccess.Kind.WRITE.ordinal)
    pushAddress()
    emit.int(access.operation.width)
    trap(MEMORY_ACCESS_OUT_OF_BOUNDS)
}

private fun LowerContext.pushUnsignedAddress(
    address: Operand,
    offset: Long,
) {
    pushInt(address)
    widenUnsigned(offset)
}

private fun LowerContext.pushUnsignedScratch(
    offset: Long,
) {
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    widenUnsigned(offset)
}

private fun LowerContext.widenUnsigned(
    offset: Long,
) {
    emit.insn(Opcodes.I2L)
    emit.long(0xFFFF_FFFFL)
    emit.insn(Opcodes.LAND)
    if (offset == 0L) return
    emit.long(offset)
    emit.insn(Opcodes.LADD)
}

private fun LowerContext.loadShort(signed: Boolean) {

    if (locals.inlineMemory == InlineMemory.UNSAFE) {
        pushUnsafeAddress()
        emit.invokeVirtual(UNSAFE_GET_INT)
        if (signed) {
            emit.int(16)
            emit.insn(Opcodes.ISHL)
            emit.int(16)
            emit.insn(Opcodes.ISHR)
        } else {
            mask(0xFFFF)
        }
        return
    }

    loadByte(0)
    mask(0xFF)
    loadByte(1)
    if (!signed) {
        mask(0xFF)
    }
    shiftOr(8)
}

private fun LowerContext.inlineStore(
    operation: MemoryOperation,
    value: Operand,
) {

    pushStored(operation, value)

    when (operation.width) {
        1 -> {
            emit.store(Opcodes.ISTORE, locals.scratchValue)
            storeByte(0)
        }
        2 -> {
            emit.store(Opcodes.ISTORE, locals.scratchValue)
            storeByte(0)
            storeByte(1)
        }
        4 -> {
            emit.store(Opcodes.ISTORE, locals.scratchValue)
            if (locals.inlineMemory == InlineMemory.UNSAFE) {
                pushUnsafeAddress()
                emit.load(Opcodes.ILOAD, locals.scratchValue)
                return emit.invokeVirtual(UNSAFE_PUT_INT)
            }
            for (index in 0 until 4) storeByte(index)
        }
        else -> {
            emit.store(Opcodes.LSTORE, locals.scratchLong[0])
            if (locals.inlineMemory == InlineMemory.UNSAFE) {
                pushUnsafeAddress()
                emit.load(Opcodes.LLOAD, locals.scratchLong[0])
                return emit.invokeVirtual(UNSAFE_PUT_LONG)
            }
            emit.load(Opcodes.LLOAD, locals.scratchLong[0])
            emit.insn(Opcodes.L2I)
            emit.store(Opcodes.ISTORE, locals.scratchValue)
            for (index in 0 until 4) storeByte(index)
            emit.load(Opcodes.LLOAD, locals.scratchLong[0])
            emit.int(32)
            emit.insn(Opcodes.LUSHR)
            emit.insn(Opcodes.L2I)
            emit.store(Opcodes.ISTORE, locals.scratchValue)
            for (index in 0 until 4) storeByte(index, offset = 4)
        }
    }
}

private fun LowerContext.pushNarrow(
    type: NumberType,
    value: Operand,
) {
    when (type) {
        NumberType.I64 -> {
            pushLong(value)
            emit.insn(Opcodes.L2I)
        }
        NumberType.F32 -> {
            pushFloat(value)
            emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
        }
        else -> pushInt(value)
    }
}

private fun LowerContext.pushBytesAt(
    index: Int,
) {
    emit.load(Opcodes.ALOAD, locals.memoryBytes)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    if (index != 0) {
        emit.int(index)
        emit.insn(Opcodes.IADD)
    }
}

private fun LowerContext.loadByte(
    index: Int,
) {
    if (locals.inlineMemory == InlineMemory.UNSAFE) {
        pushUnsafeAddress(index)
        return emit.invokeVirtual(UNSAFE_GET_BYTE)
    }
    pushBytesAt(index)
    emit.insn(Opcodes.BALOAD)
}

private fun LowerContext.storeByte(
    index: Int,
    offset: Int = 0,
) {
    val unsafe = locals.inlineMemory == InlineMemory.UNSAFE
    if (unsafe) pushUnsafeAddress(offset + index) else pushBytesAt(offset + index)
    emit.load(Opcodes.ILOAD, locals.scratchValue)
    if (index != 0) {
        emit.int(index * 8)
        emit.insn(Opcodes.IUSHR)
    }
    if (!unsafe) return emit.insn(Opcodes.BASTORE)
    emit.insn(Opcodes.I2B)
    emit.invokeVirtual(UNSAFE_PUT_BYTE)
}

private fun LowerContext.mask(
    bits: Int,
) {
    emit.int(bits)
    emit.insn(Opcodes.IAND)
}

private fun LowerContext.shiftOr(
    shift: Int,
) {
    emit.int(shift)
    emit.insn(Opcodes.ISHL)
    emit.insn(Opcodes.IOR)
}

private fun LowerContext.loadInt(
    offset: Int = 0,
) {
    if (locals.inlineMemory == InlineMemory.UNSAFE && offset == 0) {
        pushUnsafeAddress()
        return emit.invokeVirtual(UNSAFE_GET_INT)
    }
    loadByte(offset)
    mask(0xFF)
    for (index in 1 until 4) {
        loadByte(offset + index)
        if (index < 3) mask(0xFF)
        shiftOr(index * 8)
    }
}

private fun LowerContext.loadLong() {
    if (locals.inlineMemory == InlineMemory.UNSAFE) {
        pushUnsafeAddress()
        return emit.invokeVirtual(UNSAFE_GET_LONG)
    }
    loadInt(0)
    emit.insn(Opcodes.I2L)
    emit.long(0xFFFF_FFFFL)
    emit.insn(Opcodes.LAND)
    loadInt(4)
    emit.insn(Opcodes.I2L)
    emit.int(32)
    emit.insn(Opcodes.LSHL)
    emit.insn(Opcodes.LOR)
}

private fun LowerContext.pushUnsafeAddress(
    index: Int = 0,
) {
    emit.load(Opcodes.ALOAD, locals.unsafe)
    emit.load(Opcodes.ALOAD, locals.memoryBytes)
    emit.load(Opcodes.LLOAD, locals.byteArrayBase)
    emit.load(Opcodes.ILOAD, locals.scratchInt)
    if (index != 0) {
        emit.int(index)
        emit.insn(Opcodes.IADD)
    }
    emit.insn(Opcodes.I2L)
    emit.insn(Opcodes.LADD)
}

private fun LowerContext.memoryCopy(
    copy: BulkMemory.Copy,
) {
    pushResource(Resource.Memory(copy.source.index))
    pushResource(Resource.Memory(copy.destination.index))
    pushInt(copy.sourceOffset)
    pushInt(copy.destinationOffset)
    pushInt(copy.count)
    emit.invokeStatic(MEMORY_COPY)
}

private fun LowerContext.memoryFill(
    fill: BulkMemory.Fill,
) {
    pushResource(Resource.Memory(fill.memory.index))
    pushInt(fill.offset)
    pushInt(fill.value)
    pushInt(fill.count)
    emit.invokeStatic(MEMORY_FILL)
}

private fun descriptor(
    type: NumberType,
): String = when (type) {
    NumberType.I32 -> "I"
    NumberType.I64 -> "J"
    NumberType.F32 -> "F"
    NumberType.F64 -> "D"
    else -> throw Unsupported("$type memory accesses")
}
