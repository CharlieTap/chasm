package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.dexter.Conversion

internal fun LowerContext.readsInt(slot: Int): Boolean {
    return kinds.readKind(slot, SlotKindFlow.INT) == SlotKindFlow.INT
}

internal fun LowerContext.readsLong(slot: Int): Boolean {
    return kinds.readKind(slot, SlotKindFlow.LONG) == SlotKindFlow.LONG
}

internal fun LowerContext.readsFloat(slot: Int): Boolean {
    return kinds.readKind(slot, SlotKindFlow.FLOAT) == SlotKindFlow.FLOAT
}

internal fun LowerContext.readsDouble(slot: Int): Boolean {
    return kinds.readKind(slot, SlotKindFlow.DOUBLE) == SlotKindFlow.DOUBLE
}

internal inline fun LowerContext.forEachHome(
    slot: Int,
    action: (Byte) -> Unit,
) {
    forEachKind(kinds.homeMask(slot), action)
}

internal inline fun forEachKind(
    homes: Int,
    action: (Byte) -> Unit,
) {
    for (kind in NARROW_FIRST) {
        if (homes and SlotKindFlow.homeBit(kind) != 0) {
            action(kind)
        }
    }
}

internal fun anyHome(
    homes: Int,
    natural: Byte,
): Byte {

    if (homes and SlotKindFlow.homeBit(natural) != 0) {
        return natural
    }

    val order = if (SlotKindFlow.wide(natural)) WIDE_FIRST else NARROW_FIRST
    for (kind in order) {
        if (homes and SlotKindFlow.homeBit(kind) != 0) {
            return kind
        }
    }
    return SlotKindFlow.NONE
}

private val NARROW_FIRST = byteArrayOf(SlotKindFlow.INT, SlotKindFlow.FLOAT, SlotKindFlow.LONG, SlotKindFlow.DOUBLE)

private val WIDE_FIRST = byteArrayOf(SlotKindFlow.LONG, SlotKindFlow.DOUBLE, SlotKindFlow.INT, SlotKindFlow.FLOAT)

private fun LowerContext.moveResultTo(home: Int) {
    if (home < 256) {
        emit.moveResult(home)
    } else {
        emit.moveResult(Registers.NARROW)
        emit.move(home, Registers.NARROW)
    }
}

private fun LowerContext.moveResultWideTo(
    home: Int,
    pair: Int,
) {
    if (home < 256) {
        emit.moveResultWide(home)
    } else {
        emit.moveResultWide(pair)
        emit.moveWide(home, pair)
    }
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
    loadEncoded(pair, slot, kinds.readKind(slot, SlotKindFlow.LONG))
}

internal fun LowerContext.loadEncoded(
    pair: Int,
    slot: Int,
    kind: Byte,
) {
    val home = registers.home(slot, kind)
    when (kind) {
        SlotKindFlow.INT -> {
            emit.move(pair, home)
            emit.convert(Conversion.IntToLong, pair, pair)
        }
        SlotKindFlow.FLOAT -> {
            emit.move(pair, home)
            emit.invoke(FLOAT_TO_RAW_INT_BITS, pair)
            emit.moveResult(pair)
            emit.convert(Conversion.IntToLong, pair, pair)
        }
        SlotKindFlow.DOUBLE -> {
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
    storeEncoded(slot, kinds.writeKind(slot), pair)
}

internal fun LowerContext.storeEncoded(
    slot: Int,
    kind: Byte,
    pair: Int,
) {
    when {
        kind == SlotKindFlow.UNDEFINED -> Unit
        SlotKindFlow.definite(kind) -> {
            decodeInto(slot, kind, pair)
            if (kind == SlotKindFlow.LONG && roots.publishes(slot)) {
                roots.publish(slot, pair)
            }
        }
        else -> forEachHome(slot) { homeKind -> storeEncoded(slot, homeKind, pair) }
    }
}

internal fun LowerContext.decodeInto(
    slot: Int,
    kind: Byte,
    pair: Int,
) {
    val home = registers.home(slot, kind)
    when (kind) {
        SlotKindFlow.INT -> {
            emit.convert(Conversion.LongToInt, Registers.NARROW, pair)
            emit.move(home, Registers.NARROW)
        }
        SlotKindFlow.FLOAT -> {
            emit.convert(Conversion.LongToInt, Registers.NARROW, pair)
            emit.invoke(INT_BITS_TO_FLOAT, Registers.NARROW)
            moveResultTo(home)
        }
        SlotKindFlow.DOUBLE -> {
            emit.invoke(LONG_BITS_TO_DOUBLE, pair, pair + 1)
            moveResultWideTo(home, pair)
        }
        else -> emit.moveWide(home, pair)
    }
}

internal fun LowerContext.wideSource(
    operand: Operand,
    scratch: Int,
): Int {

    if (operand is Operand.Slot && readsLong(operand.slot)) {
        val home = registers.home(operand.slot, SlotKindFlow.LONG)
        if (home < 256) {
            return home
        }
    }

    loadWide(scratch, operand)
    return scratch
}

internal fun LowerContext.narrowSource(
    operand: Operand,
    scratch: Int,
): Int {

    if (operand is Operand.Slot && readsInt(operand.slot)) {
        val home = registers.home(operand.slot, SlotKindFlow.INT)
        if (home < 256) {
            return home
        }
    }

    loadInt(scratch, if (scratch == Registers.I1) Registers.W1 else Registers.W0, operand)
    return scratch
}

internal fun LowerContext.floatSource(
    operand: Operand,
    scratch: Int,
): Int {

    if (operand is Operand.Slot && readsFloat(operand.slot)) {
        val home = registers.home(operand.slot, SlotKindFlow.FLOAT)
        if (home < 256) {
            return home
        }
    }

    loadFloat(scratch, if (scratch == Registers.I1) Registers.W1 else Registers.W0, operand)
    return scratch
}

internal fun LowerContext.doubleSource(
    operand: Operand,
    scratch: Int,
): Int {

    if (operand is Operand.Slot && readsDouble(operand.slot)) {
        val home = registers.home(operand.slot, SlotKindFlow.DOUBLE)
        if (home < 256) {
            return home
        }
    }

    loadDouble(scratch, operand)
    return scratch
}

internal fun LowerContext.writeTarget(
    slot: Int,
    kind: Byte,
): Int {

    if (kinds.writeKind(slot) == kind) {
        val home = registers.home(slot, kind)
        if (home < 256) {
            return home
        }
    }

    return -1
}

internal fun LowerContext.finishWide(
    slot: Int,
    home: Int,
    register: Int,
) {
    if (home < 0) {
        store(slot, register)
    } else if (roots.publishes(slot)) {
        roots.publish(slot, register)
    }
}

internal inline fun LowerContext.writeNarrow(
    slot: Int,
    crossinline emission: (Int) -> Unit,
) {

    val home = writeTarget(slot, SlotKindFlow.INT)
    val target = if (home < 0) Registers.I0 else home
    emission(target)

    if (home < 0) {
        storeInt(slot, target)
    }
}

internal inline fun LowerContext.writeWide(
    slot: Int,
    crossinline emission: (Int) -> Unit,
) {

    val home = writeTarget(slot, SlotKindFlow.LONG)
    val target = if (home < 0) Registers.W0 else home
    emission(target)

    finishWide(slot, home, target)
}

internal inline fun LowerContext.writeFloat(
    slot: Int,
    crossinline emission: (Int) -> Unit,
) {

    val home = writeTarget(slot, SlotKindFlow.FLOAT)
    val target = if (home < 0) Registers.I0 else home
    emission(target)

    if (home < 0) {
        storeFloat(slot, target)
    }
}

internal inline fun LowerContext.writeDouble(
    slot: Int,
    crossinline emission: (Int) -> Unit,
) {

    val home = writeTarget(slot, SlotKindFlow.DOUBLE)
    val target = if (home < 0) Registers.W0 else home
    emission(target)

    if (home < 0) {
        storeDouble(slot, target)
    }
}

internal fun LowerContext.copyToSlot(
    destination: Int,
    source: Operand,
    kind: Byte = kinds.writeKind(destination),
) {

    if (source is Operand.Slot && source.slot == destination && kinds.represents(destination, kind)) {
        return
    }

    when {
        kind == SlotKindFlow.UNDEFINED -> Unit
        SlotKindFlow.definite(kind) -> copyAs(destination, kind, source)
        else -> forEachHome(destination) { homeKind -> copyAs(destination, homeKind, source) }
    }
}

private fun LowerContext.copyAs(
    destination: Int,
    kind: Byte,
    source: Operand,
) {
    val target = registers.home(destination, kind)
    when (source) {
        is Operand.Immediate -> {

            if (!SlotKindFlow.wide(kind)) {
                if (target < 256) {
                    emit.const(target, source.bits.toInt())
                } else {
                    emit.const(Registers.NARROW, source.bits.toInt())
                    emit.move(target, Registers.NARROW)
                }
                return
            }

            val register = if (target < 256) target else Registers.W0
            emit.constWide(register, source.bits)
            emit.moveWide(target, register)
            if (kind == SlotKindFlow.LONG && roots.publishes(destination)) {
                roots.publish(destination, register)
            }
        }
        is Operand.Slot -> {

            val from = kinds.readKind(source.slot, kind)
            if (from != kind) {
                loadEncoded(Registers.W0, source.slot, from)
                storeEncoded(destination, kind, Registers.W0)
                return
            }

            val origin = registers.home(source.slot, kind)
            if (!SlotKindFlow.wide(kind)) {
                emit.move(target, origin)
                return
            }

            emit.moveWide(target, origin)
            if (kind == SlotKindFlow.LONG && roots.publishes(destination)) {
                if (target < 256) {
                    roots.publish(destination, target)
                } else {
                    emit.moveWide(Registers.W0, target)
                    roots.publish(destination, Registers.W0)
                }
            }
        }
    }
}

internal fun LowerContext.copySequence(
    sources: IntArray,
    destinations: IntArray,
) {
    for (index in sources.indices) {

        val source = sources[index]
        val destination = destinations[index]
        if (source == destination) {
            continue
        }

        val kind = kinds.state(source)
        copyToSlot(destination, slot(source), kinds.representation(destination, kind))
        kinds.commitWrite(destination, kind)
    }
}

internal fun LowerContext.loadInt(
    register: Int,
    pair: Int,
    operand: Operand,
) {
    when (operand) {
        is Operand.Slot -> when (val kind = kinds.readKind(operand.slot, SlotKindFlow.INT)) {
            SlotKindFlow.INT -> emit.move(register, registers.home(operand.slot, kind))
            SlotKindFlow.FLOAT -> {
                emit.move(register, registers.home(operand.slot, kind))
                emit.invoke(FLOAT_TO_RAW_INT_BITS, register)
                emit.moveResult(register)
            }
            else -> {
                loadEncoded(pair, operand.slot, kind)
                emit.convert(Conversion.LongToInt, register, pair)
            }
        }
        is Operand.Immediate -> emit.const(register, operand.bits.toInt())
    }
}

internal fun LowerContext.storeInt(
    slot: Int,
    register: Int,
    pair: Int = Registers.W0,
) {
    when (val kind = kinds.writeKind(slot)) {
        SlotKindFlow.INT -> emit.move(registers.home(slot, kind), register)
        SlotKindFlow.FLOAT -> {
            emit.invoke(INT_BITS_TO_FLOAT, register)
            moveResultTo(registers.home(slot, kind))
        }
        else -> {
            emit.convert(Conversion.IntToLong, pair, register)
            storeEncoded(slot, kind, pair)
        }
    }
}

internal fun LowerContext.loadFloat(
    register: Int,
    pair: Int,
    operand: Operand,
) {
    when {
        operand is Operand.Immediate -> emit.const(register, operand.bits.toInt())
        operand is Operand.Slot && readsFloat(operand.slot) -> emit.move(register, registers.home(operand.slot, SlotKindFlow.FLOAT))
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
    if (kinds.writeKind(slot) == SlotKindFlow.FLOAT) {
        emit.move(registers.home(slot, SlotKindFlow.FLOAT), register)
        return
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
        operand is Operand.Immediate -> emit.constWide(pair, operand.bits)
        operand is Operand.Slot && readsDouble(operand.slot) -> emit.moveWide(pair, registers.home(operand.slot, SlotKindFlow.DOUBLE))
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

    val kind = kinds.writeKind(slot)
    if (kind == SlotKindFlow.DOUBLE) {
        emit.moveWide(registers.home(slot, kind), pair)
        return
    }

    emit.invoke(DOUBLE_TO_RAW_LONG_BITS, pair, pair + 1)
    emit.moveResultWide(pair)
    storeEncoded(slot, kind, pair)
}

internal fun LowerContext.parallelCopy(
    sources: List<Operand>,
    destinations: List<Int>,
) {

    val moves = sources.indices.filter { index ->
        val source = sources[index]
        val kind = kinds.writeKind(destinations[index])
        !(source is Operand.Slot && source.slot == destinations[index] && kinds.represents(source.slot, kind)) && kind != SlotKindFlow.UNDEFINED
    }
    if (moves.isEmpty()) return
    if (moves.size == 1) return copyToSlot(destinations[moves[0]], sources[moves[0]])

    moves.forEachIndexed { temporary, index ->

        val source = sources[index] as? Operand.Slot ?: return@forEachIndexed
        val register = registers.temporary(temporary)
        val kind = kinds.writeKind(destinations[index])

        if (canCopyDirectly(source.slot, kind)) {
            val home = registers.home(source.slot, kind)
            if (SlotKindFlow.wide(kind)) {
                emit.moveWide(register, home)
            } else {
                emit.move(register, home)
            }
        } else {
            loadWide(Registers.W0, source.slot)
            emit.moveWide(register, Registers.W0)
        }
    }

    moves.forEachIndexed { temporary, index ->

        val destination = destinations[index]
        val source = sources[index]
        if (source !is Operand.Slot) {
            return@forEachIndexed copyToSlot(destination, source)
        }

        val register = registers.temporary(temporary)
        val kind = kinds.writeKind(destination)
        if (!canCopyDirectly(source.slot, kind)) {
            emit.moveWide(Registers.W0, register)
            return@forEachIndexed storeEncoded(destination, kind, Registers.W0)
        }

        val home = registers.home(destination, kind)
        if (!SlotKindFlow.wide(kind)) {
            return@forEachIndexed emit.move(home, register)
        }

        emit.moveWide(home, register)
        if (kind == SlotKindFlow.LONG && roots.publishes(destination)) {
            emit.moveWide(Registers.W0, register)
            roots.publish(destination, Registers.W0)
        }
    }
}

private fun LowerContext.canCopyDirectly(
    source: Int,
    kind: Byte,
): Boolean {
    return SlotKindFlow.definite(kind) && kinds.readKind(source, kind) == kind
}

internal fun LowerContext.transfer(
    sources: List<Operand>,
    destinationBase: Int,
) = parallelCopy(sources, sources.indices.map { destinationBase + it })
