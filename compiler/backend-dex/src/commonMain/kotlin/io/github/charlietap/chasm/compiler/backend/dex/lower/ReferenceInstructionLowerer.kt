package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_NULL_TAG
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TAG_MASK
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.REF_CAST
import io.github.charlietap.chasm.compiler.backend.abi.REF_TEST
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.ext.nullReference
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp

internal fun lowerReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> {
            context.loadWide(Registers.W0, instruction.valueSlot)
            context.booleanResult(instruction.destinationSlot) { holds -> context.branchOnNull(Registers.W0, isNull = true, holds) }
        }
        is ReferenceInstruction.RefAsNonNullS -> context.refAsNonNull(instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefNullS -> {
            context.emit.constWide(Registers.W0, instruction.heapType.nullReference())
            context.store(instruction.destinationSlot, Registers.W0)
        }
        is ReferenceInstruction.RefFuncS -> {
            context.loadValue(Registers.W0, Resource.FunctionReference(instruction.function.index))
            context.store(instruction.destinationSlot, Registers.W0)
        }
        is ReferenceInstruction.RefEqSs -> context.refEq(instruction.reference1Slot, instruction.reference2Slot, instruction.destinationSlot)
        is ReferenceInstruction.RefCastS -> context.typeTest(REF_CAST, instruction.referenceSlot, instruction.typeTest, instruction.destinationSlot)
        is ReferenceInstruction.RefTestS -> context.typeTest(REF_TEST, instruction.referenceSlot, instruction.typeTest, instruction.destinationSlot)
    }
}

internal fun LowerContext.slotHelper(
    method: MethodRef,
    valueSlot: Int,
    destination: Int,
) {
    loadWide(Registers.W0, valueSlot)
    emit.invoke(method, Registers.W0, Registers.W0 + 1)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

internal fun LowerContext.branchOnNull(
    pair: Int,
    isNull: Boolean,
    target: String,
) {
    emit.convert(Conversion.LongToInt, Registers.I0, pair)
    emit.literal(LiteralOp.AndInt, Registers.I0, Registers.I0, REFERENCE_TAG_MASK)
    emit.const(Registers.I1, REFERENCE_NULL_TAG)
    emit.branchCompare(if (isNull) Cond.Eq else Cond.Ne, Registers.I0, Registers.I1, target)
}

private fun LowerContext.refAsNonNull(
    valueSlot: Int,
    destination: Int,
) {

    loadWide(Registers.W0, valueSlot)
    branchOnNull(Registers.W0, isNull = true, trapLabel(NULL_REFERENCE))

    val inPlace = valueSlot == destination && isLong(destination)
    if (!inPlace) {
        store(destination, Registers.W0)
    } else if (roots.publishes(destination)) {
        roots.publish(destination, Registers.W0)
    }
}

private fun LowerContext.refEq(
    reference1Slot: Int,
    reference2Slot: Int,
    destination: Int,
) {

    loadWide(Registers.W0, reference1Slot)
    loadWide(Registers.W1, reference2Slot)

    booleanResult(destination) { holds ->
        val differs = emit.freshLabel()
        emit.compare(CompareOp.CmpLong, Registers.I0, Registers.W0, Registers.W1)
        emit.branchZero(Cond.Eq, Registers.I0, holds)
        branchOnNull(Registers.W0, isNull = false, differs)
        branchOnNull(Registers.W1, isNull = true, holds)
        emit.place(differs)
    }
}

private fun LowerContext.typeTest(
    method: MethodRef,
    referenceSlot: Int,
    type: ReferenceType,
    destination: Int,
) {
    loadObject(Registers.OBJECT, Resource.TypeTest(type), REFERENCE_TYPE_TEST)
    loadWide(Registers.W0, referenceSlot)
    emit.invoke(method, Registers.CONTEXT, Registers.W0, Registers.W0 + 1, Registers.OBJECT)
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}
