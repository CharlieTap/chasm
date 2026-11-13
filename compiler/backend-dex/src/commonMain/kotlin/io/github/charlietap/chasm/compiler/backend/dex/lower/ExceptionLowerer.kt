package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_TAG
import io.github.charlietap.chasm.compiler.backend.abi.RAISE_UNWINDING
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TAKE_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.THROW_REF
import io.github.charlietap.chasm.compiler.backend.abi.THROW_TAG
import io.github.charlietap.chasm.compiler.backend.abi.UNWINDING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

private const val THROWABLE = "Ljava/lang/Throwable;"

private const val PROPAGATE = "propagate"

internal const val CHUNK_END = "chunkEnd"

internal fun LowerContext.lowerThrow(
    instruction: ControlInstruction.Throw,
) {
    val first = instruction.firstPayloadSlot
    val fields = declarations.tagFieldCount(instruction.exceptionTag)
    emit.const(Registers.I0, first + fields)
    emit.invoke(OPEN_FRAME, Registers.CONTEXT, Registers.I0)
    emit.moveResultWide(Registers.W3)
    if (fields > 0) {
        emit.convert(Conversion.LongToInt, Registers.I3, Registers.W3)
        emit.invoke(FRAME, Registers.CONTEXT)
        emit.moveResultObject(Registers.OBJECT)
        for (slot in first until first + fields) {
            loadWide(Registers.W0, slot)
            if (slot <= Short.MAX_VALUE) {
                emit.literal(LiteralOp.AddInt, Registers.I2, Registers.I3, slot)
            } else {
                emit.const(Registers.I2, slot)
                emit.binary(BinaryOp.AddInt, Registers.I2, Registers.I3, Registers.I2)
            }
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        }
    }
    loadValueInt(Registers.I0, Resource.TagAddress(instruction.exceptionTag.index))
    emit.const(Registers.I1, first)
    emit.invoke(THROW_TAG, Registers.CONTEXT, Registers.I0, Registers.I1)
    emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
    emit.gotoLabel(unwindTarget())
}

internal fun LowerContext.lowerThrowRef(
    instruction: ControlInstruction.ThrowRefS,
) {
    loadWide(Registers.W0, instruction.exceptionSlot)
    emit.invoke(THROW_REF, Registers.CONTEXT, Registers.W0, Registers.W0 + 1)
    emit.gotoLabel(unwindTarget())
}

internal fun LowerContext.unwindCheck() {
    emit.getField(Registers.I0, Registers.CONTEXT, CONTEXT_TYPE, UNWINDING_FIELD, "Z")
    if (module.analysis.usesExceptions) {
        emit.branchZero(Cond.Ne, Registers.I0, unwindTarget())
    } else {
        val done = emit.freshLabel()
        emit.branchZero(Cond.Eq, Registers.I0, done)
        emit.invoke(RAISE_UNWINDING, Registers.CONTEXT)
        emit.place(done)
    }
}

internal fun LowerContext.protects(
    position: Int,
): Boolean = regionAt(position) >= 0

private fun LowerContext.unwindTarget(): String {
    val region = regionAt(position)
    return if (region < 0) propagation() else matcher(region)
}

private fun LowerContext.matcher(
    region: Int,
): String {
    if (matchers.add(region)) matcherOrder += region
    return "match$region"
}

private fun LowerContext.propagation(): String {
    propagates = true
    return PROPAGATE
}

internal fun LowerContext.lowerExceptionHandling() {
    val regions = function.exceptions?.regions.orEmpty()
    val depths = IntArray(regions.size)
    regions.forEachIndexed { index, region -> depths[index] = region.parent?.let { parent -> depths[parent] + 1 } ?: 0 }
    val covered = regions.map { region ->
        val chunk = chunk
        if (chunk == null) region.start.index until region.end.index else maxOf(region.start.index, chunk.start) until minOf(region.end.index, chunk.end)
    }
    for (index in regions.indices.sortedByDescending { index -> depths[index] }) {
        val range = covered[index]
        if (range.isEmpty()) continue
        val end = range.last + 1
        val endLabel = if (chunk?.end == end) CHUNK_END else label(end)
        emit.catchRange(THROWABLE, label(range.first), endLabel, "pad$index")
    }
    for (index in regions.indices) {
        if (covered[index].isEmpty()) continue
        emit.beginHandler("pad$index", Registers.OBJECT)
        emit.invoke(WASM_EXCEPTION, Registers.OBJECT)
        restoreWindow(binding = true)
        emit.moveWide(Registers.W3, registers.savedStack)
        emit.invoke(CLOSE_FRAME, Registers.CONTEXT, Registers.W3, Registers.W3 + 1)
        emit.gotoLabel(matcher(index))
    }
    var emitted = 0
    while (emitted < matcherOrder.size) {
        lowerMatcher(matcherOrder[emitted])
        emitted++
    }
    if (propagates) lowerPropagation()
}

private fun LowerContext.lowerMatcher(
    region: Int,
) {
    val current = checkNotNull(function.exceptions).regions[region]
    emit.place("match$region")
    refreshMemory()
    roots.refresh()
    emit.invoke(PENDING_TAG, Registers.CONTEXT)
    emit.moveResult(Registers.I0)
    current.catches.forEach { handler ->
        val next = emit.freshLabel()
        val tag = handler.tag
        if (tag != null) {
            loadValueInt(Registers.I1, Resource.TagAddress(tag.index))
            emit.branchCompare(Cond.Ne, Registers.I0, Registers.I1, next)
        }
        val slots = handler.payloadSlots
        val fields = if (tag == null) 0 else slots.size - if (handler.exceptionReference) 1 else 0
        if (references.writesThrough) {
            clearDiscardedRoots(handler.stackSlots, slots)
        }
        emit.const(Registers.I1, maxOf(fields, 1))
        emit.newArray(Registers.OBJECT, Registers.I1, "[J")
        emit.const(Registers.I1, fields)
        emit.invoke(TAKE_EXCEPTION, Registers.CONTEXT, Registers.OBJECT, Registers.I1)
        emit.moveResultWide(Registers.W1)
        for (field in 0 until fields) {
            emit.const(Registers.I2, field)
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
            store(slots[field], Registers.W0)
        }
        if (handler.exceptionReference) store(slots.last(), Registers.W1)
        emit.gotoLabel(label(handler.target))
        emit.place(next)
    }
    val parent = current.parent
    emit.gotoLabel(if (parent == null) propagation() else matcher(parent))
}

private fun LowerContext.clearDiscardedRoots(
    stackSlots: Int,
    payload: IntArray,
) {

    if (chunk == null) {
        val slots = references.slots
        val found = slots.binarySearch(stackSlots)
        val first = if (found >= 0) found else -found - 1
        if (first < slots.size) {
            roots.clear(first, slots.size - first)
        }
        return
    }

    if (stackSlots >= frameSlots) {
        return
    }
    roots.clear(stackSlots, frameSlots - stackSlots)
    if (registers.records) {
        return
    }

    val inPayload = if (payload.any { slot -> slot >= stackSlots }) {
        BooleanArray(frameSlots).also { members -> payload.forEach { slot -> members[slot] = true } }
    } else {
        null
    }

    var zeroed = false
    for (slot in references.slots) {

        if (slot < stackSlots || inPayload?.get(slot) == true || !registers.holds(slot)) {
            continue
        }
        if (!zeroed) {
            emit.constWide(Registers.W0, 0L)
            zeroed = true
        }

        emit.moveWide(registers.home(slot), Registers.W0)
    }
}

private fun LowerContext.lowerPropagation() {
    emit.place(PROPAGATE)
    emit.const(Registers.I0, 1)
    emit.putField(Registers.I0, Registers.CONTEXT, CONTEXT_TYPE, UNWINDING_FIELD, "Z")
    roots.close()
    returnAnything()
}
