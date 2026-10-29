package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import org.objectweb.asm.Opcodes

internal val CHUNK_ARGUMENTS = listOf("I", "I")

internal fun chunkDescriptor(
    binding: String,
) = "(${binding}Lio/github/charlietap/chasm/runtime/execution/ExecutionContext;III)I"

internal fun LowerContext.lowerChunkBody() {
    val chunk = checkNotNull(chunk)
    position = SlotKinds.PROLOGUE
    initializeScratch()
    emit.long(0L)
    emit.store(Opcodes.LSTORE, locals.rootsSaved)
    emit.load(Opcodes.ILOAD, locals.argumentLocals[0])
    emit.store(Opcodes.ISTORE, locals.rootBase)
    if (function.exceptions != null) {
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.insn(Opcodes.I2L)
        emit.int(32)
        emit.insn(Opcodes.LSHL)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.int(frame.frameSlots)
        emit.insn(Opcodes.IADD)
        emit.insn(Opcodes.I2L)
        emit.insn(Opcodes.LOR)
        emit.store(Opcodes.LSTORE, locals.stackSaved)
    }
    loadSlots()
    val entries = chunk.entries
    val unknown = emit.label()
    emit.load(Opcodes.ILOAD, locals.argumentLocals[1])
    val continuationIds = chunk.continuationIds
    if (continuationIds == null) {
        emit.lookupSwitch(unknown, entries, Array(entries.size) { index -> placement(entries[index]) })
    } else {
        emit.tableSwitch(continuationIds[entries.first()], unknown, Array(entries.size) { index -> placement(entries[index]) })
    }
    emit.place(unknown)
    trap(UNREACHABLE)

    for (position in chunk.start until chunk.end) {
        this.position = position
        val label = placement(position)
        emit.place(label)
        if (module.options.lineNumbers) emit.line(position + 1, label)
        lowerInstruction(function.instructions[position], this)
    }
    emit.place(chunk.endMarker)
    if (chunk.end < function.instructions.size) {
        emit.jump(Opcodes.GOTO, label(chunk.end))
    } else {
        emit.place(bodyEnd)
        trap(UNREACHABLE)
    }
    lowerExceptionHandling()
    outOfLine.forEach { path -> path() }
    lowerExits()
}

private fun LowerContext.loadSlots() {
    if (locals.isRecording) return
    val chunk = checkNotNull(chunk)
    val reloads = chunk.reloads
    val loaded = locals.heldSlots.filter { slot -> reloads == null || reloads[slot] }
    locals.heldSlots.forEach { slot -> if (reloads != null && !reloads[slot]) initialize(slot, frame.initialValues[slot]) }
    if (loaded.isEmpty()) return
    pushContext()
    emit.invokeStatic(FRAME)
    emit.store(Opcodes.ASTORE, locals.scratchValues)
    for (slot in loaded) {
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        frameIndex(slot)
        emit.insn(Opcodes.LALOAD)
        val local = locals.slot(slot)
        when (locals.kind(slot)) {
            SlotKinds.INT -> {
                emit.insn(Opcodes.L2I)
                emit.store(Opcodes.ISTORE, local)
            }
            SlotKinds.FLOAT -> {
                emit.insn(Opcodes.L2I)
                emit.invokeStatic(INT_BITS_TO_FLOAT)
                emit.store(Opcodes.FSTORE, local)
            }
            SlotKinds.DOUBLE -> {
                emit.invokeStatic(LONG_BITS_TO_DOUBLE)
                emit.store(Opcodes.DSTORE, local)
            }
            else -> emit.store(Opcodes.LSTORE, local)
        }
        refreshShadows(slot)
    }
}

private fun LowerContext.lowerExits() {
    val chunk = checkNotNull(chunk)
    chunk.exits.forEach { (target, label) ->
        emit.place(label)
        emit.fixedWidthInt(chunk.continuation(target))
        emit.store(Opcodes.ISTORE, locals.exitPosition)
        emit.jump(Opcodes.GOTO, chunk.spill)
    }
    if ((chunk.exits.isEmpty() && !chunk.leavesAnywhere) || locals.isRecording) return
    emit.place(chunk.spill)
    val spills = chunk.spills
    val spilled = locals.heldSlots.filter { slot -> spills == null || spills[slot] }
    if (spilled.isNotEmpty()) {
        pushContext()
        emit.invokeStatic(FRAME)
        emit.store(Opcodes.ASTORE, locals.scratchValues)
    }
    for (slot in spilled) {
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        frameIndex(slot)
        val local = locals.slot(slot)
        when (locals.kind(slot)) {
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
        emit.insn(Opcodes.LASTORE)
    }
    emit.load(Opcodes.ILOAD, locals.exitPosition)
    emit.insn(Opcodes.IRETURN)
}

private fun LowerContext.frameIndex(
    slot: Int,
) {
    emit.load(Opcodes.ILOAD, locals.rootBase)
    emit.int(slot)
    emit.insn(Opcodes.IADD)
}
