package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.CAST_FAILED
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_RTT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_MATCHES
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_ARRAY_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_DEPTH_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.RTT_SUPERTYPES_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.SAME_I31
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTest
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Label

internal fun lowerManagedReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> context.refIsNull(instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefAsNonNullS -> context.refAsNonNull(instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefNullS -> context.writeReference(instruction.destinationSlot) { target -> context.emit.const(target, 0) }
        is ReferenceInstruction.RefFuncS -> context.writeReference(instruction.destinationSlot) { target ->
            context.loadObject(target, Resource.FunctionObject(instruction.function.index), MANAGED_FUNCTION)
        }
        is ReferenceInstruction.RefEqSs -> context.refEq(instruction.reference1Slot, instruction.reference2Slot, instruction.destinationSlot)
        is ReferenceInstruction.RefCastS -> context.refCast(instruction.referenceSlot, instruction.typeTest, instruction.destinationSlot)
        is ReferenceInstruction.RefTestS -> context.refTest(instruction.referenceSlot, instruction.typeTest, instruction.destinationSlot)
    }
}

private fun LowerContext.refIsNull(
    valueSlot: Int,
    destination: Int,
) {
    val reference = referenceSource(slot(valueSlot), Registers.OBJECT)
    booleanResult(destination) { holds -> emit.branchZero(Cond.Eq, reference, holds) }
}

private fun LowerContext.refAsNonNull(
    valueSlot: Int,
    destination: Int,
) {

    val reference = referenceSource(slot(valueSlot), Registers.OBJECT)
    emit.branchZero(Cond.Eq, reference, trapLabel(NULL_REFERENCE))

    if (valueSlot != destination) {
        storeReference(destination, reference)
    }
}

private fun LowerContext.refEq(
    firstSlot: Int,
    secondSlot: Int,
    destination: Int,
) {

    val first = referenceSource(slot(firstSlot), Registers.OBJECT)
    val second = referenceSource(slot(secondSlot), Registers.SECOND_OBJECT)

    booleanResult(destination) { holds ->
        emit.branchCompare(Cond.Eq, first, second, holds)
        emit.invoke(SAME_I31, first, second)
        emit.moveResult(Registers.I0)
        emit.branchZero(Cond.Ne, Registers.I0, holds)
    }
}

private fun LowerContext.refTest(
    referenceSlot: Int,
    type: ReferenceType,
    destination: Int,
) {
    val reference = referenceSource(slot(referenceSlot), Registers.OBJECT)
    booleanResult(destination) { holds -> branchOnType(reference, type, holds) }
}

private fun LowerContext.refCast(
    referenceSlot: Int,
    type: ReferenceType,
    destination: Int,
) {

    val reference = referenceSource(slot(referenceSlot), Registers.OBJECT)
    val matches = emit.freshLabel()
    branchOnType(reference, type, matches)
    trap(CAST_FAILED)
    emit.place(matches)

    if (referenceSlot != destination) {
        storeReference(destination, reference)
    }
}

internal fun LowerContext.branchOnType(
    reference: Int,
    type: ReferenceType,
    matches: Label,
) {

    val fails = emit.freshLabel()
    emit.branchZero(Cond.Eq, reference, if (type.nullable) matches else fails)

    when (val test = module.referenceTests.test(type.heapType)) {
        ReferenceTest.Always -> emit.gotoLabel(matches)
        ReferenceTest.Never -> Unit
        ReferenceTest.NotFunction -> {
            branchOnInstance(reference, MANAGED_FUNCTION, fails)
            emit.gotoLabel(matches)
        }
        is ReferenceTest.InstanceOf -> branchOnInstance(reference, test.descriptor, matches)
        is ReferenceTest.Defined -> branchOnRtt(reference, test.typeIndex, matches, fails)
        is ReferenceTest.FunctionType -> {
            emit.moveObject(Registers.THIRD_OBJECT, reference)
            loadValueInt(Registers.I1, Resource.RuntimeType(test.typeIndex))
            emit.moveObject(reference, Registers.THIRD_OBJECT)
            emit.invoke(FUNCTION_MATCHES, Registers.CONTEXT, reference, Registers.I1)
            emit.moveResult(Registers.I0)
            emit.branchZero(Cond.Ne, Registers.I0, matches)
        }
    }

    emit.place(fails)
}

private fun LowerContext.branchOnInstance(
    reference: Int,
    descriptor: String,
    target: Label,
) {
    emit.instanceOf(Registers.I0, reference, descriptor)
    emit.branchZero(Cond.Ne, Registers.I0, target)
}

private fun LowerContext.branchOnRtt(
    reference: Int,
    typeIndex: Int,
    matches: Label,
    fails: Label,
) {

    val test = module.rttTests.test(typeIndex)

    emit.instanceOf(Registers.I0, reference, test.baseClass)
    emit.branchZero(Cond.Eq, Registers.I0, fails)

    emit.moveObject(Registers.THIRD_OBJECT, reference)
    emit.checkCast(Registers.THIRD_OBJECT, test.baseClass)
    emit.getField(Registers.THIRD_OBJECT, Registers.THIRD_OBJECT, test.baseClass, COMPILED_RTT_FIELD, RTT_TYPE)
    loadObject(Registers.SECOND_OBJECT, Resource.Rtt(typeIndex), RTT_TYPE)
    emit.branchCompare(Cond.Eq, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, matches)

    if (test.final) {
        return
    }

    if (test.depth > 0) {
        emit.getField(Registers.I0, Registers.THIRD_OBJECT, RTT_TYPE, RTT_DEPTH_FIELD, "I")
        emit.const(Registers.I1, test.depth)
        emit.branchCompare(Cond.Lt, Registers.I0, Registers.I1, fails)
    }

    emit.getField(Registers.THIRD_OBJECT, Registers.THIRD_OBJECT, RTT_TYPE, RTT_SUPERTYPES_FIELD, RTT_ARRAY_TYPE)
    emit.const(Registers.I1, test.depth)
    emit.arrayGet(ArrayKind.Object, Registers.THIRD_OBJECT, Registers.THIRD_OBJECT, Registers.I1)
    emit.branchCompare(Cond.Eq, Registers.THIRD_OBJECT, Registers.SECOND_OBJECT, matches)
}
