package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.ExceptionCatch
import io.github.charlietap.chasm.cir.ExceptionRegion
import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.CREATE_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_EXCEPTION_TAG
import io.github.charlietap.chasm.compiler.backend.abi.RAISE_UNWINDING
import io.github.charlietap.chasm.compiler.backend.abi.RETHROW_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TAKE_MANAGED_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.THROW_EXCEPTION
import io.github.charlietap.chasm.compiler.backend.abi.UNWINDING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_WORDS
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindFlow
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Label

private const val OBJECT_ARRAY = "[Ljava/lang/Object;"

internal fun LowerContext.lowerThrow(
    instruction: ControlInstruction.Throw,
) {
    throwManaged(instruction)
}

internal fun LowerContext.lowerThrowRef(
    instruction: ControlInstruction.ThrowRefS,
) {
    loadReference(Registers.OBJECT, instruction.exceptionSlot)
    emit.invoke(RETHROW_EXCEPTION, Registers.CONTEXT, Registers.OBJECT)
    emit.gotoLabel(unwindTarget())
}

private fun LowerContext.throwManaged(
    instruction: ControlInstruction.Throw,
) {

    val handler = if (chunk == null) localHandler(instruction.exceptionTag) else null
    if (handler != null && handler.tag == null && !handler.exceptionReference) {
        return emit.gotoLabel(label(handler.target))
    }

    val fields = declarations.tagFieldTypes(instruction.exceptionTag)
    val references = fields.any { type -> type is ValueType.Reference }
    newLanes(fields.size, words = fields.any { type -> type !is ValueType.Reference }, references = references)

    fields.forEachIndexed { index, type ->
        val slot = instruction.firstPayloadSlot + index
        emit.const(Registers.I1, index)
        if (type is ValueType.Reference) {
            loadReference(Registers.OBJECT, slot)
            emit.arrayPut(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
        } else {
            loadWide(Registers.W0, slot)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
        }
    }

    loadValueInt(Registers.I0, Resource.TagAddress(instruction.exceptionTag.index))
    if (handler == null) {
        emit.invoke(THROW_EXCEPTION, Registers.CONTEXT, Registers.I0, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT)
        return emit.gotoLabel(unwindTarget())
    }

    emit.invoke(CREATE_EXCEPTION, Registers.CONTEXT, Registers.I0, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT)
    emit.moveResultObject(Registers.OBJECT)
    deliverException(handler)
}

private fun LowerContext.localHandler(
    thrown: TagIndex,
): ExceptionCatch? {

    val regions = function.exceptions?.regions ?: return null
    var region = regionAt(position)
    while (region >= 0) {
        val current = regions[region]
        for (handler in current.catches) {
            val tag = handler.tag ?: return handler
            if (tag.index == thrown.index) {
                return handler
            }
            if (declarations.isImported(tag) && declarations.isImported(thrown)) {
                return null
            }
        }
        region = current.parent ?: -1
    }

    return null
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

private fun LowerContext.unwindTarget(): Label {
    val region = regionAt(position)
    return if (region < 0) propagation() else matcher(region)
}

private fun LowerContext.matcher(region: Int): Label {
    if (matchers.add(region)) matcherOrder += region
    return matcherLabels[region]
}

private fun LowerContext.propagation(): Label {
    propagates = true
    return propagationLabel
}

internal fun LowerContext.lowerExceptionHandling() {
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
    emit.place(matcherLabels[region])
    refreshMemory()
    lowerManagedMatcher(current)
}

private fun LowerContext.lowerManagedMatcher(
    current: ExceptionRegion,
) {

    emit.invoke(PENDING_EXCEPTION_TAG, Registers.CONTEXT)
    emit.moveResult(Registers.I0)

    current.catches.forEach { handler ->

        val next = emit.freshLabel()
        val tag = handler.tag
        if (tag != null) {
            loadValueInt(Registers.I1, Resource.TagAddress(tag.index))
            emit.branchCompare(Cond.Ne, Registers.I0, Registers.I1, next)
        }

        emit.invoke(TAKE_MANAGED_EXCEPTION, Registers.CONTEXT)
        emit.moveResultObject(Registers.OBJECT)
        deliverException(handler)
        emit.place(next)
    }

    val parent = current.parent
    emit.gotoLabel(if (parent == null) propagation() else matcher(parent))
}

private fun LowerContext.deliverException(
    handler: ExceptionCatch,
) {

    val slots = handler.payloadSlots
    if (handler.exceptionReference) {
        emit.moveObject(registers.home(slots.last(), SlotKindFlow.OBJECT), Registers.OBJECT)
    }

    val tag = handler.tag
    val fields = if (tag == null) emptyList() else declarations.tagFieldTypes(tag)
    if (fields.isNotEmpty()) {
        emit.getField(Registers.SECOND_OBJECT, Registers.OBJECT, WASM_EXCEPTION_TYPE, WASM_EXCEPTION_WORDS, "[J")
        emit.getField(Registers.THIRD_OBJECT, Registers.OBJECT, WASM_EXCEPTION_TYPE, WASM_EXCEPTION_REFERENCES, OBJECT_ARRAY)
    }
    fields.forEachIndexed { field, type ->
        emit.const(Registers.I1, field)
        if (type is ValueType.Reference) {
            emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
            emit.moveObject(registers.home(slots[field], SlotKindFlow.OBJECT), Registers.OBJECT)
        } else {
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
            storeEncoded(slots[field], kinds.handlerKind(slots[field]), Registers.W0)
        }
    }

    emit.gotoLabel(label(handler.target))
}

private fun LowerContext.lowerPropagation() {
    emit.place(propagationLabel)
    emit.const(Registers.I0, 1)
    emit.putField(Registers.I0, Registers.CONTEXT, CONTEXT_TYPE, UNWINDING_FIELD, "Z")
    returnAnything()
}
