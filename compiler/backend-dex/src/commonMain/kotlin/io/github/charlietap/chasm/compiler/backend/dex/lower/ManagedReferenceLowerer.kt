package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.CAST_FAILED
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_MATCHES
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.SAME_I31
import io.github.charlietap.chasm.compiler.backend.abi.WASM_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EQ
import io.github.charlietap.chasm.compiler.backend.abi.WASM_EXCEPTION_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.WASM_I31
import io.github.charlietap.chasm.compiler.backend.abi.WASM_STRUCT
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
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
            context.loadObject(target, Resource.FunctionObject(instruction.function.index), FUNCTION_INSTANCE)
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

    when (val heap = type.heapType) {
        is HeapType.Abstract -> when (heap.type) {
            AbstractHeapType.Any -> {
                branchOnInstance(reference, FUNCTION_INSTANCE, fails)
                emit.gotoLabel(matches)
            }
            AbstractHeapType.Extern -> emit.gotoLabel(matches)
            AbstractHeapType.Eq -> branchOnInstance(reference, WASM_EQ, matches)
            AbstractHeapType.Struct -> branchOnInstance(reference, WASM_STRUCT, matches)
            AbstractHeapType.Array -> branchOnInstance(reference, WASM_ARRAY, matches)
            AbstractHeapType.I31 -> branchOnInstance(reference, WASM_I31, matches)
            AbstractHeapType.Func -> branchOnInstance(reference, FUNCTION_INSTANCE, matches)
            AbstractHeapType.Exception -> branchOnInstance(reference, WASM_EXCEPTION_TYPE, matches)
            AbstractHeapType.None, AbstractHeapType.NoFunc, AbstractHeapType.NoExtern, AbstractHeapType.NoException -> Unit
        }
        is HeapType.Concrete -> {
            val index = heap.type.index
            if (module.gc.isFunction(index)) {
                emit.moveObject(Registers.THIRD_OBJECT, reference)
                loadValueInt(Registers.I1, Resource.RuntimeType(index))
                emit.moveObject(reference, Registers.THIRD_OBJECT)
                emit.invoke(FUNCTION_MATCHES, Registers.CONTEXT, reference, Registers.I1)
                emit.moveResult(Registers.I0)
                emit.branchZero(Cond.Ne, Registers.I0, matches)
            } else {
                branchOnInstance(reference, module.gc.typeDescriptor(index), matches)
            }
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
