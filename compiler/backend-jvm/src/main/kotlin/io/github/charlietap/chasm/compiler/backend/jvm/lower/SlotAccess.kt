package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.analysis.NarrowHomeMismatch
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.NumberType
import org.objectweb.asm.Opcodes

private fun LowerContext.checkNarrowWrite(
    slot: Int,
) {
    if (!slotKinds.accountsFor(position, slot)) throw NarrowHomeMismatch(slot, position)
}

internal fun LowerContext.pushInt(
    operand: Operand,
) {
    when (operand) {
        is Operand.Immediate -> emit.int(operand.bits.toInt())
        is Operand.Slot -> {
            val local = locals.slot(operand.slot)
            when (locals.kind(operand.slot)) {
                SlotKinds.INT -> emit.load(Opcodes.ILOAD, local)
                SlotKinds.FLOAT -> {
                    emit.load(Opcodes.FLOAD, local)
                    emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
                }
                SlotKinds.DOUBLE -> {
                    emit.load(Opcodes.DLOAD, local)
                    emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
                    emit.insn(Opcodes.L2I)
                }
                else -> {
                    emit.load(Opcodes.LLOAD, local)
                    emit.insn(Opcodes.L2I)
                }
            }
        }
    }
}

internal fun LowerContext.pushLong(
    operand: Operand,
) {
    when (operand) {
        is Operand.Immediate -> emit.long(operand.bits)
        is Operand.Slot -> {
            val local = locals.slot(operand.slot)
            when (locals.kind(operand.slot)) {
                SlotKinds.INT -> {
                    emit.load(Opcodes.ILOAD, local)
                    emit.insn(Opcodes.I2L)
                }
                SlotKinds.FLOAT -> {
                    emit.load(Opcodes.FLOAD, local)
                    emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
                    emit.insn(Opcodes.I2L)
                }
                SlotKinds.DOUBLE -> {
                    emit.load(Opcodes.DLOAD, local)
                    emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
                }
                else -> emit.load(Opcodes.LLOAD, local)
            }
        }
    }
}

internal fun LowerContext.pushFloat(
    operand: Operand,
) {
    if (operand is Operand.Immediate) return emit.float(operand.bits.toInt())
    val float = floatHome((operand as Operand.Slot).slot)
    if (float >= 0) return emit.load(Opcodes.FLOAD, float)
    pushInt(operand)
    emit.invokeStatic(INT_BITS_TO_FLOAT)
}

internal fun LowerContext.pushDouble(
    operand: Operand,
) {
    if (operand is Operand.Immediate) return emit.double(operand.bits)
    val double = doubleHome((operand as Operand.Slot).slot)
    if (double >= 0) return emit.load(Opcodes.DLOAD, double)
    pushLong(operand)
    emit.invokeStatic(LONG_BITS_TO_DOUBLE)
}

private fun LowerContext.floatHome(
    slot: Int,
): Int =
    if (locals.kind(slot) == SlotKinds.FLOAT) locals.slot(slot) else locals.floatShadow(slot)

private fun LowerContext.doubleHome(
    slot: Int,
): Int =
    if (locals.kind(slot) == SlotKinds.DOUBLE) locals.slot(slot) else locals.doubleShadow(slot)

internal fun LowerContext.refreshShadows(
    slot: Int,
) {
    val float = locals.floatShadow(slot)
    if (float >= 0) {
        pushInt(Operand.Slot(slot))
        emit.invokeStatic(INT_BITS_TO_FLOAT)
        emit.store(Opcodes.FSTORE, float)
    }
    val double = locals.doubleShadow(slot)
    if (double >= 0) {
        pushLong(Operand.Slot(slot))
        emit.invokeStatic(LONG_BITS_TO_DOUBLE)
        emit.store(Opcodes.DSTORE, double)
    }
}

internal fun LowerContext.initializeShadows(
    slot: Int,
    bits: Long,
) {
    val float = locals.floatShadow(slot)
    if (float >= 0) {
        emit.float(bits.toInt())
        emit.store(Opcodes.FSTORE, float)
    }
    val double = locals.doubleShadow(slot)
    if (double >= 0) {
        emit.double(bits)
        emit.store(Opcodes.DSTORE, double)
    }
}

internal fun LowerContext.push(
    type: NumberType,
    operand: Operand,
) = when (type) {
    NumberType.I32 -> pushInt(operand)
    NumberType.I64 -> pushLong(operand)
    NumberType.F32 -> pushFloat(operand)
    NumberType.F64 -> pushDouble(operand)
    else -> throw Unsupported("$type values")
}

internal fun LowerContext.storeInt(
    slot: Int,
) {
    storeOwnInt(slot)
    refreshShadows(slot)
}

internal fun LowerContext.storeLong(
    slot: Int,
) {
    storeOwnLong(slot)
    refreshShadows(slot)
}

private fun LowerContext.storeOwnInt(
    slot: Int,
) {
    val local = locals.slot(slot)
    when (locals.kind(slot)) {
        SlotKinds.INT -> {
            checkNarrowWrite(slot)
            emit.store(Opcodes.ISTORE, local)
        }
        SlotKinds.FLOAT -> {
            checkNarrowWrite(slot)
            emit.invokeStatic(INT_BITS_TO_FLOAT)
            emit.store(Opcodes.FSTORE, local)
        }
        else -> {
            emit.insn(Opcodes.I2L)
            storeOwnLong(slot)
        }
    }
}

private fun LowerContext.storeOwnLong(
    slot: Int,
) {
    val local = locals.slot(slot)
    when (locals.kind(slot)) {
        SlotKinds.INT -> {
            checkNarrowWrite(slot)
            emit.insn(Opcodes.L2I)
            emit.store(Opcodes.ISTORE, local)
        }
        SlotKinds.FLOAT -> {
            checkNarrowWrite(slot)
            emit.insn(Opcodes.L2I)
            emit.invokeStatic(INT_BITS_TO_FLOAT)
            emit.store(Opcodes.FSTORE, local)
        }
        SlotKinds.DOUBLE -> {
            emit.invokeStatic(LONG_BITS_TO_DOUBLE)
            emit.store(Opcodes.DSTORE, local)
        }
        else -> {
            emit.store(Opcodes.LSTORE, local)
            if (references.writesThrough && references.isReference[slot]) publish(slot)
        }
    }
}

internal fun LowerContext.storeFloat(
    slot: Int,
) {
    if (locals.kind(slot) == SlotKinds.FLOAT) {
        checkNarrowWrite(slot)
        return emit.store(Opcodes.FSTORE, locals.slot(slot))
    }
    val shadow = locals.floatShadow(slot)
    if (shadow >= 0) {
        emit.insn(Opcodes.DUP)
        emit.store(Opcodes.FSTORE, shadow)
    }
    emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
    storeOwnInt(slot)
}

internal fun LowerContext.storeDouble(
    slot: Int,
) {
    if (locals.kind(slot) == SlotKinds.DOUBLE) return emit.store(Opcodes.DSTORE, locals.slot(slot))
    val shadow = locals.doubleShadow(slot)
    if (shadow >= 0) {
        emit.insn(Opcodes.DUP2)
        emit.store(Opcodes.DSTORE, shadow)
    }
    emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
    storeOwnLong(slot)
}

internal fun LowerContext.store(
    type: NumberType,
    slot: Int,
) = when (type) {
    NumberType.I32 -> storeInt(slot)
    NumberType.I64 -> storeLong(slot)
    NumberType.F32 -> storeFloat(slot)
    NumberType.F64 -> storeDouble(slot)
    else -> throw Unsupported("$type values")
}

internal fun LowerContext.copyToSlot(
    destination: Int,
    source: Operand,
) {
    if (source is Operand.Slot && source.slot == destination) return
    copyOwn(destination, source)
    if (source !is Operand.Slot) return
    val float = locals.floatShadow(destination)
    val sourceFloat = floatHome(source.slot)
    if (float >= 0 && sourceFloat >= 0) {
        emit.load(Opcodes.FLOAD, sourceFloat)
        emit.store(Opcodes.FSTORE, float)
    }
    val double = locals.doubleShadow(destination)
    val sourceDouble = doubleHome(source.slot)
    if (double >= 0 && sourceDouble >= 0) {
        emit.load(Opcodes.DLOAD, sourceDouble)
        emit.store(Opcodes.DSTORE, double)
    }
}

private fun LowerContext.copyOwn(
    destination: Int,
    source: Operand,
) {
    if (source is Operand.Slot) {
        val kind = locals.kind(source.slot)
        if (kind == locals.kind(destination)) {
            when (kind) {
                SlotKinds.INT -> {
                    emit.load(Opcodes.ILOAD, locals.slot(source.slot))
                    return storeInt(destination)
                }
                SlotKinds.FLOAT -> {
                    emit.load(Opcodes.FLOAD, locals.slot(source.slot))
                    return storeFloat(destination)
                }
                SlotKinds.DOUBLE -> {
                    emit.load(Opcodes.DLOAD, locals.slot(source.slot))
                    return storeDouble(destination)
                }
            }
        }
    }
    pushLong(source)
    storeLong(destination)
}

internal fun LowerContext.parallelCopy(
    sources: List<Operand>,
    destinations: List<Int>,
) {
    val moves = sources.indices.filter { index ->
        val source = sources[index]
        !(source is Operand.Slot && source.slot == destinations[index])
    }
    if (moves.size == 1) return copyToSlot(destinations[moves[0]], sources[moves[0]])
    moves.forEach { index -> pushLong(sources[index]) }
    moves.asReversed().forEach { index -> storeLong(destinations[index]) }
}

internal fun LowerContext.transfer(
    sources: List<Operand>,
    destinationBase: Int,
) =
    parallelCopy(sources, sources.indices.map { destinationBase + it })
