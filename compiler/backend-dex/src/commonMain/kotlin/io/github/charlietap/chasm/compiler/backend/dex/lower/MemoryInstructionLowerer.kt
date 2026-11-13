package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_ACCESS_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_BYTE_ARRAY_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_COPY
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_FILL
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_SIZE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_GET_INT
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_GET_LONG
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
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun lowerMemoryInstruction(
    instruction: MemoryInstruction,
    context: LowerContext,
) {
    val access = instruction.access()
    if (access != null) return context.memoryAccess(access)
    when (val bulk = instruction.bulk()) {
        is BulkMemory.Copy -> return context.memoryCopy(bulk.source, bulk.destination, bulk.count, bulk.sourceOffset, bulk.destinationOffset)
        is BulkMemory.Fill -> return context.memoryFill(bulk.memory, bulk.count, bulk.value, bulk.offset)
        null -> Unit
    }
    val call = instruction.helperCall() ?: throw Unsupported(instruction::class.simpleName ?: "memory instruction")
    context.lowerHelperCall(call)
}

internal fun LowerContext.refreshMemory() {
    if (!analysis.usesInlineMemory) return
    val memory = checkNotNull(module.inlineMemory)
    loadMemory(Registers.OBJECT, memory)
    emit.getField(Registers.I0, Registers.OBJECT, MEMORY_INSTANCE, MEMORY_BYTE_ARRAY_FIELD, "[B")
    emit.moveObject(registers.memoryBytes, Registers.I0)
    emit.invokeVirtual(MEMORY_SIZE, Registers.OBJECT)
    emit.moveResult(registers.memorySize)
}

private fun LowerContext.loadMemory(
    register: Int,
    memory: MemoryIndex,
) = loadObject(register, Resource.Memory(memory.index), MEMORY_INSTANCE)

private fun LowerContext.memoryAccess(
    access: MemoryAccess,
) {
    if (analysis.usesInlineMemory && access.memory == module.inlineMemory) return inlineAccess(access)
    val type = descriptor(access.operation.type)
    val name = access.operation.helperName
    loadMemory(Registers.OBJECT, access.memory)
    loadInt(Registers.I0, Registers.W0, access.address)
    emit.const(Registers.I1, access.offset)
    val value = access.value
    if (value == null) {
        emit.invoke(memoryAccess(name, value = null, result = type), Registers.OBJECT, Registers.I0, Registers.I1)
        when (type) {
            "I" -> {
                emit.moveResult(Registers.I2)
                storeInt(access.destination, Registers.I2)
            }
            "F" -> {
                emit.moveResult(Registers.I2)
                storeFloat(access.destination, Registers.I2)
            }
            "D" -> {
                emit.moveResultWide(Registers.W2)
                storeDouble(access.destination, Registers.W2)
            }
            else -> {
                emit.moveResultWide(Registers.W2)
                store(access.destination, Registers.W2)
            }
        }
        return
    }
    when (type) {
        "I" -> {
            loadInt(Registers.I2, Registers.W1, value)
            emit.invoke(memoryAccess(name, value = type, result = null), Registers.OBJECT, Registers.I0, Registers.I1, Registers.I2)
        }
        "F" -> {
            loadFloat(Registers.I2, Registers.W1, value)
            emit.invoke(memoryAccess(name, value = type, result = null), Registers.OBJECT, Registers.I0, Registers.I1, Registers.I2)
        }
        "D" -> {
            loadDouble(Registers.W1, value)
            emit.invoke(memoryAccess(name, value = type, result = null), Registers.OBJECT, Registers.I0, Registers.I1, Registers.W1, Registers.W1 + 1)
        }
        else -> {
            loadWide(Registers.W1, value)
            emit.invoke(memoryAccess(name, value = type, result = null), Registers.OBJECT, Registers.I0, Registers.I1, Registers.W1, Registers.W1 + 1)
        }
    }
}

private fun descriptor(
    type: NumberType,
): String = when (type) {
    NumberType.I32 -> "I"
    NumberType.F32 -> "F"
    NumberType.F64 -> "D"
    else -> "J"
}

private fun LowerContext.inlineAccess(
    access: MemoryAccess,
) {
    val width = access.operation.width
    if (access.offset < 0) {
        emit.gotoLabel(outOfBounds(access))
        return
    }
    val address = access.address
    if (address is Operand.Immediate) {
        immediateAccess(access, address)
        return
    }
    if (analysis.checkedAccesses[position]) {
        checkedAccess(access)
        return
    }
    val outOfBounds = outOfBounds(access)
    val reach = access.offset.toLong() + width
    if (reach > Int.MAX_VALUE) {
        emit.gotoLabel(outOfBounds)
        return
    }
    loadInt(Registers.I0, Registers.W0, access.address)
    emit.branchZero(Cond.Lt, Registers.I0, outOfBounds)
    if (reach <= -Byte.MIN_VALUE) {
        emit.literal(LiteralOp.AddInt, Registers.I3, registers.memorySize, -reach.toInt())
    } else {
        emit.const(Registers.I3, reach.toInt())
        emit.binary(BinaryOp.SubInt, Registers.I3, registers.memorySize, Registers.I3)
    }
    emit.branchCompare(Cond.Gt, Registers.I0, Registers.I3, outOfBounds)
    addOffset(access.offset)
    inlineTransfer(access)
}

private fun LowerContext.immediateAccess(
    access: MemoryAccess,
    address: Operand.Immediate,
) {

    val width = access.operation.width
    val effective = (address.bits and UNSIGNED_MASK) + access.offset
    if (effective + width > Int.MAX_VALUE) {
        emit.gotoLabel(outOfBounds(access))
        return
    }

    emit.const(Registers.I0, effective.toInt())
    if (!analysis.checkedAccesses[position] && effective + width > declarations.memoryMinimumBytes(access.memory)) {
        emit.literal(LiteralOp.AddInt, Registers.I3, registers.memorySize, -width)
        emit.branchCompare(Cond.Gt, Registers.I0, Registers.I3, outOfBounds(access))
    }

    inlineTransfer(access)
}

private fun LowerContext.checkedAccess(
    access: MemoryAccess,
) {
    loadInt(Registers.I0, Registers.W0, access.address)
    addOffset(access.offset)
    inlineTransfer(access)
}

private fun LowerContext.inlineTransfer(access: MemoryAccess) {
    val value = access.value
    if (value == null) {
        inlineLoad(access.operation, access.destination)
    } else {
        inlineStore(access.operation.width, value)
    }
}

private fun LowerContext.addOffset(
    offset: Int,
) {
    if (offset == 0) return
    if (offset <= Short.MAX_VALUE) {
        emit.literal(LiteralOp.AddInt, Registers.I0, Registers.I0, offset)
    } else {
        emit.const(Registers.I1, offset)
        emit.binary(BinaryOp.AddInt, Registers.I0, Registers.I0, Registers.I1)
    }
}

private fun LowerContext.outOfBounds(access: MemoryAccess): String {

    if (!module.options.stacktraces) {
        return trapLabel(MEMORY_OUT_OF_BOUNDS)
    }

    val label = emit.freshLabel()
    val origin = position
    outOfLine += {

        emit.place(label)
        emit.line(line(origin))

        loadMemory(Registers.OBJECT, access.memory)
        emit.const(Registers.I2, if (access.value == null) ACCESS_READ else ACCESS_WRITE)
        loadInt(Registers.I1, Registers.W0, access.address)
        emit.convert(Conversion.IntToLong, Registers.W1, Registers.I1)
        emit.constWide(Registers.W2, UNSIGNED_MASK)
        emit.binary(BinaryOp.AndLong, Registers.W1, Registers.W1, Registers.W2)
        emit.constWide(Registers.W2, access.offset.toLong() and UNSIGNED_MASK)
        emit.binary(BinaryOp.AddLong, Registers.W1, Registers.W1, Registers.W2)
        emit.const(Registers.I3, access.operation.width)

        emit.invoke(MEMORY_ACCESS_OUT_OF_BOUNDS, Registers.OBJECT, Registers.I2, Registers.W1, Registers.W1 + 1, Registers.I3)
        returnUnreachable()
    }

    return label
}

private const val ACCESS_READ = 0

private const val ACCESS_WRITE = 1

private const val UNSIGNED_MASK = 0xFFFF_FFFFL

private fun LowerContext.unsafeOffset() {
    emit.convert(Conversion.IntToLong, Registers.W1, Registers.I0)
    emit.binary(BinaryOp.AddLong, registers.unsafeOffset, Registers.W1, registers.byteArrayBase)
}

private fun LowerContext.inlineLoad(
    operation: MemoryOperation,
    destination: Int,
) {
    val width = operation.width
    val wideType = operation.type == NumberType.I64 || operation.type == NumberType.F64
    if (analysis.unsafeMemory) return unsafeLoad(operation, destination)
    if (width == 8) {
        assembleInt(Registers.I1, 0, 4, signedTop = true)
        emit.convert(Conversion.IntToLong, Registers.W1, Registers.I1)
        emit.constWide(Registers.W2, 0xFFFF_FFFFL)
        emit.binary(BinaryOp.AndLong, Registers.W1, Registers.W1, Registers.W2)
        assembleInt(Registers.I1, 4, 4, signedTop = true)
        emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
        emit.const(Registers.I3, 32)
        emit.binary(BinaryOp.ShlLong, Registers.W0, Registers.W0, Registers.I3)
        emit.binary(BinaryOp.OrLong, Registers.W0, Registers.W0, Registers.W1)
        return store(destination, Registers.W0)
    }
    assembleInt(Registers.I1, 0, width, signedTop = width == 4 || operation.signed)
    if (!wideType) return storeInt(destination, Registers.I1)
    emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
    if (operation == MemoryOperation.I64Load32U) {
        emit.constWide(Registers.W2, 0xFFFF_FFFFL)
        emit.binary(BinaryOp.AndLong, Registers.W0, Registers.W0, Registers.W2)
    }
    store(destination, Registers.W0)
}

private fun LowerContext.unsafeLoad(
    operation: MemoryOperation,
    destination: Int,
) {
    val width = operation.width
    if (width > 1) unsafeOffset()
    if (width == 8) {
        emit.invokeVirtualRange(UNSAFE_GET_LONG, registers.unsafe)
        if (operation.type != NumberType.I64) {
            emit.moveResultWide(Registers.W0)
            return store(destination, Registers.W0)
        }
        val target = wideTarget(destination)
        emit.moveResultWide(target)
        return finishWide(destination, target)
    }
    if (width == 4 && operation.type == NumberType.I32) {
        emit.invokeVirtualRange(UNSAFE_GET_INT, registers.unsafe)
        val target = narrowTarget(destination)
        emit.moveResult(target)
        return finishNarrow(destination, target)
    }
    if (width == 1) {
        emit.arrayGet(ArrayKind.Byte, Registers.I1, registers.memoryBytes, Registers.I0)
    } else {
        emit.invokeVirtualRange(UNSAFE_GET_INT, registers.unsafe)
        emit.moveResult(Registers.I1)
    }
    when {
        width == 2 && operation.signed -> emit.convert(Conversion.IntToShort, Registers.I1, Registers.I1)
        width == 2 -> emit.convert(Conversion.IntToChar, Registers.I1, Registers.I1)
        width == 1 && !operation.signed -> emit.literal(LiteralOp.AndInt, Registers.I1, Registers.I1, 0xFF)
    }
    if (operation.type != NumberType.I64) return storeInt(destination, Registers.I1)
    emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
    if (operation == MemoryOperation.I64Load32U) {
        emit.constWide(Registers.W2, 0xFFFF_FFFFL)
        emit.binary(BinaryOp.AndLong, Registers.W0, Registers.W0, Registers.W2)
    }
    store(destination, Registers.W0)
}

private fun LowerContext.assembleInt(
    target: Int,
    base: Int,
    count: Int,
    signedTop: Boolean,
) {
    for (k in 0 until count) {
        val register = if (k == 0) target else Registers.I2
        val index = if (base + k == 0) {
            Registers.I0
        } else {
            emit.literal(LiteralOp.AddInt, Registers.I3, Registers.I0, base + k)
            Registers.I3
        }
        emit.arrayGet(ArrayKind.Byte, register, registers.memoryBytes, index)
        val top = k == count - 1
        if (!(top && signedTop)) emit.literal(LiteralOp.AndInt, register, register, 0xFF)
        if (k > 0) {
            emit.literal(LiteralOp.ShlInt, register, register, 8 * k)
            emit.binary(BinaryOp.OrInt, target, target, register)
        }
    }
}

private fun LowerContext.inlineStore(
    width: Int,
    value: Operand,
) {
    if (analysis.unsafeMemory && width >= 4) {
        if (width == 4) {
            if (value is Operand.Slot && isInt(value.slot)) {
                emit.move(registers.unsafeValue, registers.home(value.slot))
            } else {
                loadInt(Registers.I1, Registers.W0, value)
                emit.move(registers.unsafeValue, Registers.I1)
            }
            unsafeOffset()
            return emit.invokeVirtualRange(UNSAFE_PUT_INT, registers.unsafe)
        }
        if (value is Operand.Slot && isLong(value.slot)) {
            emit.moveWide(registers.unsafeValue, registers.home(value.slot))
        } else {
            loadWide(Registers.W2, value)
            emit.moveWide(registers.unsafeValue, Registers.W2)
        }
        unsafeOffset()
        return emit.invokeVirtualRange(UNSAFE_PUT_LONG, registers.unsafe)
    }
    if (width < 8) {
        val source = if (value is Operand.Slot && isInt(value.slot) && registers.home(value.slot) < 256) {
            registers.home(value.slot)
        } else {
            loadInt(Registers.I1, Registers.W1, value)
            Registers.I1
        }
        return disassembleInt(0, width, source)
    }
    loadWide(Registers.W1, value)
    emit.convert(Conversion.LongToInt, Registers.I1, Registers.W1)
    disassembleInt(0, 4)
    emit.const(Registers.I2, 32)
    emit.binary(BinaryOp.UshrLong, Registers.W2, Registers.W1, Registers.I2)
    emit.convert(Conversion.LongToInt, Registers.I1, Registers.W2)
    disassembleInt(4, 4)
}

private fun LowerContext.disassembleInt(
    base: Int,
    count: Int,
    source: Int = Registers.I1,
) {
    for (k in 0 until count) {
        val value = if (k == 0) {
            source
        } else {
            emit.literal(LiteralOp.UshrInt, Registers.I2, source, 8 * k)
            Registers.I2
        }
        val index = if (base + k == 0) {
            Registers.I0
        } else {
            emit.literal(LiteralOp.AddInt, Registers.I3, Registers.I0, base + k)
            Registers.I3
        }
        emit.arrayPut(ArrayKind.Byte, value, registers.memoryBytes, index)
    }
}

private fun LowerContext.memoryCopy(
    source: MemoryIndex,
    destination: MemoryIndex,
    count: Operand,
    sourceOffset: Operand,
    destinationOffset: Operand,
) {
    loadMemory(Registers.W0, source)
    loadMemory(Registers.W0 + 1, destination)
    loadInt(Registers.I0, Registers.W1, count)
    loadInt(Registers.I1, Registers.W1, sourceOffset)
    loadInt(Registers.I2, Registers.W1, destinationOffset)
    emit.invoke(MEMORY_COPY, Registers.W0, Registers.W0 + 1, Registers.I1, Registers.I2, Registers.I0)
}

private fun LowerContext.memoryFill(
    memory: MemoryIndex,
    count: Operand,
    value: Operand,
    offset: Operand,
) {
    loadMemory(Registers.W0, memory)
    loadInt(Registers.I0, Registers.W1, count)
    loadInt(Registers.I1, Registers.W1, value)
    loadInt(Registers.I2, Registers.W1, offset)
    emit.invoke(MEMORY_FILL, Registers.W0, Registers.I2, Registers.I1, Registers.I0)
}
