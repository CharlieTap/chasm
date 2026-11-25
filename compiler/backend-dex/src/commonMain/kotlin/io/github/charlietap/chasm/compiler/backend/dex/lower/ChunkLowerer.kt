package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindFlow
import io.github.charlietap.dexter.ArrayKind

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
    emit.moveObject(registers.objectFrame, registers.parameter(2))
    if (chunk.usesMemory) refreshMemory()
    if (chunk.usesMemory && analysis.unsafeMemory) {
        loadObject(registers.unsafe, Resource.Unsafe, UNSAFE_TYPE)
        emit.constWide(registers.byteArrayBase, checkNotNull(module.options.byteArrayBaseOffset))
        module.emittedByteArrayBase = true
    }
    analysis.cachedConstants.forEachIndexed { index, constant ->
        if (chunk.constants?.get(index) == false) return@forEachIndexed
        loadCachedConstant(constant)
        emit.moveObject(registers.cachedConstant(index), Registers.OBJECT)
    }
    cachesLoaded = true
    openWindow()
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
        if (registers.holds(slot, SlotKindFlow.OBJECT)) {
            reloadReference(slot)
        }
        if (registers.heldMask(slot) and SlotKindFlow.homeBit(SlotKindFlow.OBJECT).inv() == 0) {
            continue
        }
        if (!framed) {
            loadFrameArray()
            framed = true
        }

        frameIndex(Registers.I2, slot)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        forEachKind(registers.heldMask(slot)) { kind -> if (kind != SlotKindFlow.OBJECT) decodeInto(slot, kind, Registers.W0) }
    }
}

private fun LowerContext.reloadReference(slot: Int) {
    emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
    emit.const(Registers.I1, slot)
    emit.arrayGet(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, Registers.I1)
    emit.moveObject(registers.home(slot, SlotKindFlow.OBJECT), Registers.THIRD_OBJECT)
}

internal fun LowerContext.spillReference(
    slot: Int,
    register: Int,
) {
    emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
    emit.const(Registers.I1, slot)
    emit.arrayPut(ArrayKind.Object, register, Registers.SECOND_OBJECT, Registers.I1)
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
    if (kind == SlotKindFlow.OBJECT) {
        emit.moveObject(Registers.THIRD_OBJECT, registers.home(slot, kind))
        return spillReference(slot, Registers.THIRD_OBJECT)
    }
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

            val held = registers.heldMask(slot)
            val kind = if (held == SlotKindFlow.homeBit(SlotKindFlow.OBJECT)) SlotKindFlow.OBJECT else anyHome(held, SlotKindFlow.LONG)
            check(kind != SlotKindFlow.NONE)
            spill(slot, kind)
        }
    }

    emit.returnValue(Registers.I0)
}
