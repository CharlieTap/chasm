package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.analysis.NarrowHomeMismatch
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.dexter.Conversion

internal fun LowerContext.isNarrow(
    slot: Int,
) = slotKinds.kind(slot).let { it == SlotKinds.INT || it == SlotKinds.FLOAT }

internal fun LowerContext.isInt(
    slot: Int,
) = slotKinds.kind(slot) == SlotKinds.INT

internal fun LowerContext.isFloat(
    slot: Int,
) = slotKinds.kind(slot) == SlotKinds.FLOAT

internal fun LowerContext.isDouble(
    slot: Int,
) = slotKinds.kind(slot) == SlotKinds.DOUBLE

internal fun LowerContext.isLong(
    slot: Int,
) = slotKinds.kind(slot) == SlotKinds.LONG

internal fun LowerContext.checkNarrowWrite(
    slot: Int,
) {
    if (!slotKinds.accountsFor(position, slot)) throw NarrowHomeMismatch(slot, position)
}

internal fun LowerContext.loadWide(
    pair: Int,
    operand: Operand,
) {
    when (operand) {
        is Operand.Slot -> loadWide(pair, operand.slot)
        is Operand.Immediate -> emit.constWide(pair, operand.bits)
    }
}

internal fun LowerContext.loadWide(
    pair: Int,
    slot: Int,
) {
    val home = registers.home(slot)
    when (slotKinds.kind(slot)) {
        SlotKinds.INT -> {
            emit.move(pair, home)
            emit.convert(Conversion.IntToLong, pair, pair)
        }
        SlotKinds.FLOAT -> {
            emit.move(pair, home)
            emit.invoke(FLOAT_TO_RAW_INT_BITS, pair)
            emit.moveResult(pair)
            emit.convert(Conversion.IntToLong, pair, pair)
        }
        SlotKinds.DOUBLE -> {
            emit.moveWide(pair, home)
            emit.invoke(DOUBLE_TO_RAW_LONG_BITS, pair, pair + 1)
            emit.moveResultWide(pair)
        }
        else -> emit.moveWide(pair, home)
    }
}

internal fun LowerContext.store(
    slot: Int,
    pair: Int,
) {
    val home = registers.home(slot)
    when (slotKinds.kind(slot)) {
        SlotKinds.INT -> {
            checkNarrowWrite(slot)
            emit.convert(Conversion.LongToInt, Registers.NARROW, pair)
            emit.move(home, Registers.NARROW)
        }
        SlotKinds.FLOAT -> {
            checkNarrowWrite(slot)
            emit.convert(Conversion.LongToInt, Registers.NARROW, pair)
            emit.invoke(INT_BITS_TO_FLOAT, Registers.NARROW)
            emit.moveResult(home)
        }
        SlotKinds.DOUBLE -> {
            emit.invoke(LONG_BITS_TO_DOUBLE, pair, pair + 1)
            emit.moveResultWide(home)
        }
        else -> {
            emit.moveWide(home, pair)
            if (references.writesThrough && references.isReference[slot] && publishesWrites) publish(slot, pair)
        }
    }
}

internal fun LowerContext.wideSource(
    operand: Operand,
    scratch: Int,
): Int =
    if (operand is Operand.Slot && isLong(operand.slot) && registers.home(operand.slot) < 256) {
        registers.home(operand.slot)
    } else {
        loadWide(scratch, operand)
        scratch
    }

internal fun LowerContext.narrowSource(
    operand: Operand,
    scratch: Int,
): Int =
    if (operand is Operand.Slot && isInt(operand.slot) && registers.home(operand.slot) < 256) {
        registers.home(operand.slot)
    } else {
        loadInt(scratch, if (scratch == Registers.I1) Registers.W1 else Registers.W0, operand)
        scratch
    }

internal fun LowerContext.narrowTarget(
    slot: Int,
): Int =
    if (isInt(slot) && registers.home(slot) < 256) {
        checkNarrowWrite(slot)
        registers.home(slot)
    } else {
        Registers.I0
    }

internal fun LowerContext.finishNarrow(
    slot: Int,
    register: Int,
) {
    if (register != registers.home(slot)) storeInt(slot, register)
}

internal fun LowerContext.wideTarget(
    slot: Int,
): Int = registers.home(slot).takeIf { it < 256 && isLong(slot) } ?: Registers.W0

internal fun LowerContext.finishWide(
    slot: Int,
    register: Int,
) {
    if (register != registers.home(slot)) return store(slot, register)
    if (references.writesThrough && references.isReference[slot] && publishesWrites) publish(slot, register)
}

internal fun LowerContext.floatSource(
    operand: Operand,
    scratch: Int,
): Int =
    if (operand is Operand.Slot && isFloat(operand.slot)) {
        registers.home(operand.slot)
    } else {
        loadFloat(scratch, if (scratch == Registers.I1) Registers.W1 else Registers.W0, operand)
        scratch
    }

internal fun LowerContext.doubleSource(
    operand: Operand,
    scratch: Int,
): Int =
    if (operand is Operand.Slot && isDouble(operand.slot)) {
        registers.home(operand.slot)
    } else {
        loadDouble(scratch, operand)
        scratch
    }

internal fun LowerContext.copyToSlot(
    destination: Int,
    source: Operand,
) {
    when (source) {
        is Operand.Immediate -> {
            if (isDouble(destination)) return emit.constWide(registers.home(destination), source.bits)
            if (isNarrow(destination)) {
                checkNarrowWrite(destination)
                val home = registers.home(destination)
                if (home < 256) return emit.const(home, source.bits.toInt())
                emit.const(Registers.NARROW, source.bits.toInt())
                return emit.move(home, Registers.NARROW)
            }
            val target = wideTarget(destination)
            emit.constWide(target, source.bits)
            finishWide(destination, target)
        }
        is Operand.Slot -> {
            if (source.slot == destination) return
            val kind = slotKinds.kind(source.slot)
            if (kind != slotKinds.kind(destination)) {
                loadWide(Registers.W0, source.slot)
                return store(destination, Registers.W0)
            }
            if (kind == SlotKinds.INT || kind == SlotKinds.FLOAT) {
                checkNarrowWrite(destination)
                return emit.move(registers.home(destination), registers.home(source.slot))
            }
            emit.moveWide(registers.home(destination), registers.home(source.slot))
            if (references.writesThrough && references.isReference[destination] && publishesWrites) {
                val home = registers.home(destination)
                if (home < 256) {
                    publish(destination, home)
                } else {
                    emit.moveWide(Registers.W0, home)
                    publish(destination, Registers.W0)
                }
            }
        }
    }
}

internal fun LowerContext.loadInt(
    register: Int,
    pair: Int,
    operand: Operand,
) {
    when (operand) {
        is Operand.Slot -> if (isInt(operand.slot)) {
            emit.move(register, registers.home(operand.slot))
        } else if (isFloat(operand.slot)) {
            emit.move(register, registers.home(operand.slot))
            emit.invoke(FLOAT_TO_RAW_INT_BITS, register)
            emit.moveResult(register)
        } else {
            loadWide(pair, operand)
            emit.convert(Conversion.LongToInt, register, pair)
        }
        is Operand.Immediate -> emit.const(register, operand.bits.toInt())
    }
}

internal fun LowerContext.storeInt(
    slot: Int,
    register: Int,
    pair: Int = Registers.W0,
) {
    if (isInt(slot)) {
        checkNarrowWrite(slot)
        return emit.move(registers.home(slot), register)
    }
    if (isFloat(slot)) {
        checkNarrowWrite(slot)
        emit.move(Registers.NARROW, register)
        emit.invoke(INT_BITS_TO_FLOAT, Registers.NARROW)
        return emit.moveResult(registers.home(slot))
    }
    emit.convert(Conversion.IntToLong, pair, register)
    store(slot, pair)
}

internal fun LowerContext.loadFloat(
    register: Int,
    pair: Int,
    operand: Operand,
) {
    when {
        operand is Operand.Slot && isFloat(operand.slot) -> emit.move(register, registers.home(operand.slot))
        operand is Operand.Immediate -> emit.const(register, operand.bits.toInt())
        else -> {
            loadInt(register, pair, operand)
            emit.invoke(INT_BITS_TO_FLOAT, register)
            emit.moveResult(register)
        }
    }
}

internal fun LowerContext.storeFloat(
    slot: Int,
    register: Int,
) {
    if (isFloat(slot)) {
        checkNarrowWrite(slot)
        return emit.move(registers.home(slot), register)
    }
    emit.invoke(FLOAT_TO_RAW_INT_BITS, register)
    emit.moveResult(register)
    storeInt(slot, register)
}

internal fun LowerContext.loadDouble(
    pair: Int,
    operand: Operand,
) {
    when {
        operand is Operand.Slot && isDouble(operand.slot) -> emit.moveWide(pair, registers.home(operand.slot))
        operand is Operand.Immediate -> emit.constWide(pair, operand.bits)
        else -> {
            loadWide(pair, operand)
            emit.invoke(LONG_BITS_TO_DOUBLE, pair, pair + 1)
            emit.moveResultWide(pair)
        }
    }
}

internal fun LowerContext.storeDouble(
    slot: Int,
    pair: Int,
) {
    if (isDouble(slot)) return emit.moveWide(registers.home(slot), pair)
    emit.invoke(DOUBLE_TO_RAW_LONG_BITS, pair, pair + 1)
    emit.moveResultWide(pair)
    store(slot, pair)
}

internal fun LowerContext.parallelCopy(
    sources: List<Operand>,
    destinations: List<Int>,
) {
    val moves = sources.indices.filter { index ->
        val source = sources[index]
        !(source is Operand.Slot && source.slot == destinations[index])
    }
    if (moves.isEmpty()) return
    if (moves.size == 1) return copyToSlot(destinations[moves[0]], sources[moves[0]])
    moves.forEachIndexed { temporary, index ->
        when (val source = sources[index]) {
            is Operand.Slot -> if (!isLong(source.slot)) {
                loadWide(Registers.W0, source.slot)
                emit.moveWide(registers.temporary(temporary), Registers.W0)
            } else {
                emit.moveWide(registers.temporary(temporary), registers.home(source.slot))
            }
            is Operand.Immediate -> {
                emit.constWide(Registers.W0, source.bits)
                emit.moveWide(registers.temporary(temporary), Registers.W0)
            }
        }
    }
    moves.forEachIndexed { temporary, index ->
        val destination = destinations[index]
        if (!isLong(destination)) {
            emit.moveWide(Registers.W0, registers.temporary(temporary))
            return@forEachIndexed store(destination, Registers.W0)
        }
        emit.moveWide(registers.home(destination), registers.temporary(temporary))
        if (references.writesThrough && references.isReference[destination] && publishesWrites) {
            emit.moveWide(Registers.W0, registers.temporary(temporary))
            publish(destination, Registers.W0)
        }
    }
}

internal fun LowerContext.transfer(
    sources: List<Operand>,
    destinationBase: Int,
) = parallelCopy(sources, sources.indices.map { destinationBase + it })
