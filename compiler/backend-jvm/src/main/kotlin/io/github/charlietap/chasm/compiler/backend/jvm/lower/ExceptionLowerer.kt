package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.compiler.backend.abi.CLOSE_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_EXCEPTION_TAG
import io.github.charlietap.chasm.compiler.backend.abi.RAISE_UNWINDING
import io.github.charlietap.chasm.compiler.backend.abi.RETHROW_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TAKE_MANAGED_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.THROW_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.UNWINDING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_WORDS
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.jvm.abi.THROWABLE
import io.github.charlietap.chasm.type.ValueType
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

    val fields = declarations.tagFieldTypes(tag)
    newLanes(fields.size, references = fields.any { field -> field is ValueType.Reference })
    fields.forEachIndexed { index, field ->
        if (field is ValueType.Reference) {
            emit.load(Opcodes.ALOAD, locals.scratchReferences)
            emit.int(index)
            pushObject(slot(firstPayloadSlot + index))
            emit.insn(Opcodes.AASTORE)
        } else {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(index)
            pushLong(slot(firstPayloadSlot + index))
            emit.insn(Opcodes.LASTORE)
        }
    }

    pushContext()
    pushResource(Resource.TagAddress(tag.index))
    emit.load(Opcodes.ALOAD, locals.scratchValues)
    emit.load(Opcodes.ALOAD, locals.scratchReferences)
    emit.invokeStatic(THROW_EXCEPTION)
    emit.jump(Opcodes.GOTO, unwindTarget(position))
}

internal fun LowerContext.lowerThrowRef(
    exceptionSlot: Int,
) {
    pushContext()
    pushObject(slot(exceptionSlot))
    emit.invokeStatic(RETHROW_EXCEPTION)
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
    emit.invokeStatic(PENDING_EXCEPTION_TAG)
    emit.store(Opcodes.ISTORE, locals.scratchInt)
    current.catches.forEach { handler ->
        val next = emit.label()
        val tag = handler.tag
        if (tag != null) {
            emit.load(Opcodes.ILOAD, locals.scratchInt)
            pushResource(Resource.TagAddress(tag.index))
            emit.jump(Opcodes.IF_ICMPNE, next)
        }
        pushContext()
        emit.invokeStatic(TAKE_MANAGED_EXCEPTION)
        emit.store(Opcodes.ASTORE, locals.scratchObject)

        val slots = handler.payloadSlots
        if (handler.exceptionReference) {
            emit.load(Opcodes.ALOAD, locals.scratchObject)
            storeObject(slots.last())
        }
        val fields = if (tag == null) emptyList() else declarations.tagFieldTypes(tag)
        fields.forEachIndexed { field, type ->
            emit.load(Opcodes.ALOAD, locals.scratchObject)
            emit.type(Opcodes.CHECKCAST, WASM_EXCEPTION_TYPE)
            if (type is ValueType.Reference) {
                emit.getField(WASM_EXCEPTION_TYPE, WASM_EXCEPTION_REFERENCES, OBJECT_ARRAY)
                emit.int(field)
                emit.insn(Opcodes.AALOAD)
                storeObject(slots[field])
            } else {
                emit.getField(WASM_EXCEPTION_TYPE, WASM_EXCEPTION_WORDS, "[J")
                emit.int(field)
                emit.insn(Opcodes.LALOAD)
                storeLong(slots[field])
            }
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
    returnNothing()
}

private const val OBJECT_ARRAY = "[Ljava/lang/Object;"
