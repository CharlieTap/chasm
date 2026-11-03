package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.Conversion

internal fun LowerContext.lowerChunkBody() {
    val chunk = checkNotNull(chunk)
    val publication = references.publication
    publishesWrites = publication.afterEntry
    lowerChunkPrologue(chunk)
    for (position in chunk.start until chunk.end) {
        emit.place(label(position))
        this.position = position
        publishesWrites = publication.requiresPublicationAfter(position)
        if (module.options.stacktraces) emit.line(line(position))
        lowerInstruction(function.instructions[position], this)
    }
    publishesWrites = true
    emit.place(CHUNK_END)
    if (chunk.end < function.instructions.size) {
        emit.gotoLabel(label(chunk.end))
    } else {
        emit.place(bodyEnd)
        returnUnreachable()
    }
    lowerExceptionHandling()
    if (outOfBoundsUsed) {
        emit.place(OUT_OF_BOUNDS)
        emit.invoke(MEMORY_OUT_OF_BOUNDS)
        returnUnreachable()
    }
    outOfLine.forEach { path -> path() }
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
    if (references.writesThrough) {
        emit.move(registers.activationFp, registers.frameBase)
        refreshActivation()
    }
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
    position = SlotKinds.PROLOGUE
    reloadSlots()
    if (analysis.window.callBufferWords > 0) {
        emit.const(Registers.I0, 0)
        emit.moveObject(registers.callBuffer, Registers.I0)
    }
    emit.move(Registers.I0, registers.parameter(1))
    emit.packedSwitch(Registers.I0, 0, chunk.entries.map { entry -> label(entry) })
    emit.invoke(UNREACHABLE)
    returnUnreachable()
}

private fun LowerContext.heldSlots(): List<Int> =
    if (registers.records) emptyList() else (0 until frameSlots).filter { slot -> registers.holds(slot) }

private fun LowerContext.reloadSlots() {
    val reloads = chunk?.reloads
    val held = heldSlots().filter { slot -> reloads == null || reloads[slot] }
    if (held.isEmpty()) return
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    for (slot in held) {
        frameIndex(Registers.I2, slot)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        val home = registers.home(slot)
        when (slotKinds.kind(slot)) {
            SlotKinds.INT -> {
                emit.convert(Conversion.LongToInt, Registers.NARROW, Registers.W0)
                emit.move(home, Registers.NARROW)
            }
            SlotKinds.FLOAT -> {
                emit.convert(Conversion.LongToInt, Registers.NARROW, Registers.W0)
                emit.invoke(INT_BITS_TO_FLOAT, Registers.NARROW)
                emit.moveResult(home)
            }
            SlotKinds.DOUBLE -> {
                emit.invoke(LONG_BITS_TO_DOUBLE, Registers.W0, Registers.W0 + 1)
                emit.moveResultWide(home)
            }
            else -> emit.moveWide(home, Registers.W0)
        }
    }
}

private fun LowerContext.lowerExits(
    chunk: Chunk,
) {
    if (chunk.exits.isEmpty() && !chunk.leavesAnywhere) return
    for (target in chunk.exits.toList()) {
        emit.place(exitLabel(target))
        emit.const(Registers.I0, target)
        emit.gotoLabel(SPILL)
    }
    emit.place(SPILL)
    val spills = chunk.spills
    val held = heldSlots().filter { slot -> spills == null || spills[slot] }
    if (held.isNotEmpty()) {
        emit.invoke(FRAME, Registers.CONTEXT)
        emit.moveResultObject(Registers.OBJECT)
    }
    for (slot in held) {
        val home = registers.home(slot)
        when (slotKinds.kind(slot)) {
            SlotKinds.INT -> {
                emit.move(Registers.I1, home)
                emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
            }
            SlotKinds.FLOAT -> {
                emit.move(Registers.I1, home)
                emit.invoke(FLOAT_TO_RAW_INT_BITS, Registers.I1)
                emit.moveResult(Registers.I1)
                emit.convert(Conversion.IntToLong, Registers.W0, Registers.I1)
            }
            SlotKinds.DOUBLE -> {
                emit.moveWide(Registers.W0, home)
                emit.invoke(DOUBLE_TO_RAW_LONG_BITS, Registers.W0, Registers.W0 + 1)
                emit.moveResultWide(Registers.W0)
            }
            else -> emit.moveWide(Registers.W0, home)
        }
        frameIndex(Registers.I2, slot)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    emit.returnValue(Registers.I0)
}

internal const val SPILL = "spill"
