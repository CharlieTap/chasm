package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Conversion

internal fun LowerContext.lowerChunkBody() {

    val chunk = checkNotNull(chunk)
    enterPrologue()
    lowerChunkPrologue(chunk)

    lowerPositions(chunk.start, chunk.end)
    emit.place(chunkEndLabel)
    if (chunk.end < function.instructions.size) {
        emit.gotoLabel(label(chunk.end))
    } else {
        emit.place(bodyEnd)
        returnUnreachable()
    }

    lowerOutOfLine()
    lowerExits(chunk)
}

private fun LowerContext.lowerChunkPrologue(
    chunk: Chunk,
) {
    emit.moveObject(Registers.CONTEXT, registers.contextParameter)
    emit.move(Registers.DEPTH, registers.depthParameter)
    emit.moveObject(registers.binding, registers.bindingParameter)
    emit.move(registers.frameBase, registers.parameter(0))
    if (chunk.usesMemory) refreshMemory()
    if (chunk.usesMemory && analysis.unsafeMemory) {
        loadObject(registers.unsafe, Resource.Unsafe, UNSAFE_TYPE)
        emit.constWide(registers.byteArrayBase, checkNotNull(module.options.byteArrayBaseOffset))
    }
    roots.initialize()
    analysis.cachedConstants.forEachIndexed { index, constant ->
        if (chunk.constants?.get(index) == false) return@forEachIndexed
        loadCachedConstant(constant)
        emit.moveObject(registers.cachedConstant(index), Registers.OBJECT)
    }
    cachesLoaded = true
    openWindow()
    if (function.exceptions != null && chunk.protects) {
        emit.move(Registers.I0, registers.frameBase)
        emit.convert(Conversion.IntToLong, Registers.W0, Registers.I0)
        emit.const(Registers.I1, 32)
        emit.binary(BinaryOp.ShlLong, Registers.W0, Registers.W0, Registers.I1)
        frameIndex(Registers.I2, frameSlots)
        emit.convert(Conversion.IntToLong, Registers.W1, Registers.I2)
        emit.binary(BinaryOp.OrLong, Registers.W0, Registers.W0, Registers.W1)
        emit.moveWide(registers.savedStack, Registers.W0)
    }
    reloadSlots()
    if (facts.window.callBufferWords > 0) {
        emit.const(Registers.I0, 0)
        emit.moveObject(registers.callBuffer, Registers.I0)
    }
    emit.move(Registers.I0, registers.parameter(1))
    emit.packedSwitch(Registers.I0, 0, chunk.entries.map { entry -> label(entry) })
    trap(UNREACHABLE)
}

private fun LowerContext.reloadSlots() {

    if (registers.records) {
        return
    }
    val reloads = chunk?.reloads

    var framed = false
    for (slot in 0 until frameSlots) {

        if (!registers.holds(slot) || (reloads != null && !reloads[slot])) {
            continue
        }
        if (!framed) {
            loadFrameArray()
            framed = true
        }

        frameIndex(Registers.I2, slot)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        forEachKind(registers.heldMask(slot)) { kind -> decodeInto(slot, kind, Registers.W0) }
    }
}

private fun LowerContext.loadFrameArray() {
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
}

private fun hasSingleHome(homes: Int): Boolean {
    return homes.countOneBits() == 1
}

internal fun mixedSlots(
    registers: Registers,
    homes: RegisterHomes?,
    spills: BooleanArray,
): IntArray {

    if (homes == null) {
        return IntArray(0)
    }

    var count = 0
    for (slot in spills.indices) {
        if (mixed(registers, homes, spills, slot)) {
            count++
        }
    }

    val slots = IntArray(count)
    var next = 0
    for (slot in spills.indices) {
        if (mixed(registers, homes, spills, slot)) {
            slots[next++] = slot
        }
    }
    return slots
}

private fun mixed(
    registers: Registers,
    homes: RegisterHomes,
    spills: BooleanArray,
    slot: Int,
): Boolean {
    return spills[slot] && registers.holds(slot) && !hasSingleHome(homes.mask(slot))
}

private fun LowerContext.spill(
    slot: Int,
    kind: Byte,
) {
    loadEncoded(Registers.W0, slot, kind)
    frameIndex(Registers.I2, slot)
    emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
}

internal fun LowerContext.spillMixed(chunk: Chunk) {
    var framed = false
    for (slot in chunk.mixed) {

        val current = kinds.state(slot)
        val kind = when {
            SlotKindFlow.definite(current) -> current
            current == SlotKindFlow.ANY_NARROW || current == SlotKindFlow.ANY_WIDE -> anyHome(registers.heldMask(slot), SlotKindFlow.LONG)
            else -> SlotKindFlow.NONE
        }
        if (kind == SlotKindFlow.NONE || !registers.holds(slot, kind)) {
            continue
        }

        if (!framed) {
            loadFrameArray()
            framed = true
        }
        spill(slot, kind)
    }
}

private fun LowerContext.lowerExits(
    chunk: Chunk,
) {

    if (chunk.exits.isEmpty() && !chunk.leavesAnywhere) return

    for (target in chunk.exits.toList()) {
        emit.place(exitLabel(target))
        kinds.moveTo(target)
        spillMixed(chunk)
        emit.const(Registers.I0, chunk.continuation(target))
        emit.gotoLabel(spillLabel)
    }

    emit.place(spillLabel)
    val spills = chunk.spills
    var framed = false
    if (!registers.records) {
        for (slot in 0 until frameSlots) {

            if (!registers.holds(slot) || !hasSingleHome(kinds.homeMask(slot)) || (spills != null && !spills[slot])) {
                continue
            }
            if (!framed) {
                loadFrameArray()
                framed = true
            }

            val kind = anyHome(registers.heldMask(slot), SlotKindFlow.LONG)
            check(kind != SlotKindFlow.NONE)
            spill(slot, kind)
        }
    }

    emit.returnValue(Registers.I0)
}
