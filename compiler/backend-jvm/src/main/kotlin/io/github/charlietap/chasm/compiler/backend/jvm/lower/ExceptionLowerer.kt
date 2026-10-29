package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.TagIndex
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
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.jvm.abi.THROWABLE
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

private fun LowerContext.unwindTarget(
    position: Int,
): Label {
    val region = regionAt(position)
    return if (region < 0) propagation() else matcher(region)
}

internal fun LowerContext.unwindCheck() {
    pushContext()
    emit.getField(CONTEXT_TYPE, UNWINDING_FIELD, "Z")
    if (module.analysis.usesExceptions) return emit.jump(Opcodes.IFNE, unwindTarget(position))
    val continued = emit.label()
    emit.jump(Opcodes.IFEQ, continued)
    pushContext()
    emit.invokeStatic(RAISE_UNWINDING)
    emit.place(continued)
}

internal fun LowerContext.lowerThrow(
    tag: TagIndex,
    firstPayloadSlot: Int,
) {
    val fields = declarations.tagFieldCount(tag)
    pushContext()
    emit.int(firstPayloadSlot + fields)
    emit.invokeStatic(OPEN_FRAME)
    emit.store(Opcodes.LSTORE, locals.scratchLong[0])
    for (field in 0 until fields) {
        pushContext()
        emit.invokeStatic(FRAME)
        emit.load(Opcodes.LLOAD, locals.scratchLong[0])
        emit.insn(Opcodes.L2I)
        emit.int(firstPayloadSlot + field)
        emit.insn(Opcodes.IADD)
        pushLong(slot(firstPayloadSlot + field))
        emit.insn(Opcodes.LASTORE)
    }
    pushContext()
    pushResource(Resource.TagAddress(tag.index))
    emit.int(firstPayloadSlot)
    emit.invokeStatic(THROW_TAG)
    pushContext()
    emit.load(Opcodes.LLOAD, locals.scratchLong[0])
    emit.invokeStatic(CLOSE_FRAME)
    emit.jump(Opcodes.GOTO, unwindTarget(position))
}

internal fun LowerContext.lowerThrowRef(
    exceptionSlot: Int,
) {
    pushContext()
    pushLong(slot(exceptionSlot))
    emit.invokeStatic(THROW_REF)
    emit.jump(Opcodes.GOTO, unwindTarget(position))
}

internal fun LowerContext.lowerExceptionHandling() {
    val regions = function.exceptions?.regions.orEmpty()
    for (index in regions.indices.reversed()) {
        val region = regions[index]
        val start = maxOf(region.start.index, chunk?.start ?: 0)
        val end = minOf(region.end.index, chunk?.end ?: function.instructions.size)
        if (start >= end) continue
        val pad = emit.label()
        val endLabel = if (chunk != null && end == chunk.end) chunk.endMarker else placement(end)
        emit.tryCatch(placement(start), endLabel, pad, THROWABLE)
        emit.place(pad, THROWABLE.removePrefix("L").removeSuffix(";"))
        emit.invokeStatic(WASM_EXCEPTION)
        emit.insn(Opcodes.POP)
        pushContext()
        emit.load(Opcodes.LLOAD, locals.stackSaved)
        emit.invokeStatic(CLOSE_FRAME)
        emit.jump(Opcodes.GOTO, matcher(index))
    }
    var emitted = 0
    while (emitted < matcherOrder.size) {
        val region = matcherOrder[emitted]
        emitMatcher(region, matchers.getValue(region))
        emitted++
    }
    propagationLabel?.let { emitPropagation(it) }
}

private fun LowerContext.matcher(
    region: Int,
): Label = matchers.getOrPut(region) {
    matcherOrder += region
    emit.label()
}

private fun LowerContext.propagation(): Label = propagationLabel ?: emit.label().also { propagationLabel = it }

private fun LowerContext.emitMatcher(
    region: Int,
    entry: Label,
) {
    val regions = checkNotNull(function.exceptions).regions
    val current = regions[region]
    emit.place(entry)
    refreshMemory()
    pushContext()
    emit.invokeStatic(PENDING_TAG)
    emit.store(Opcodes.ISTORE, locals.scratchInt)
    current.catches.forEach { handler ->
        val next = emit.label()
        val tag = handler.tag
        if (tag != null) {
            emit.load(Opcodes.ILOAD, locals.scratchInt)
            pushResource(Resource.TagAddress(tag.index))
            emit.jump(Opcodes.IF_ICMPNE, next)
        }
        val slots = handler.payloadSlots
        val fields = if (tag == null) 0 else slots.size - if (handler.exceptionReference) 1 else 0
        emit.int(maxOf(fields, 1))
        emit.newLongArray()
        emit.store(Opcodes.ASTORE, locals.scratchValues)
        pushContext()
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.int(fields)
        emit.invokeStatic(TAKE_EXCEPTION)
        if (handler.exceptionReference) storeLong(slots.last()) else emit.insn(Opcodes.POP2)
        for (field in 0 until fields) {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(field)
            emit.insn(Opcodes.LALOAD)
            storeLong(slots[field])
        }
        emit.jump(Opcodes.GOTO, label(handler.target))
        emit.place(next)
    }
    val parent = current.parent
    emit.jump(Opcodes.GOTO, if (parent == null) propagation() else matcher(parent))
}

private fun LowerContext.emitPropagation(
    entry: Label,
) {
    emit.place(entry)
    pushContext()
    emit.int(1)
    emit.putField(CONTEXT_TYPE, UNWINDING_FIELD, "Z")
    closeRoots()
    returnNothing()
}
