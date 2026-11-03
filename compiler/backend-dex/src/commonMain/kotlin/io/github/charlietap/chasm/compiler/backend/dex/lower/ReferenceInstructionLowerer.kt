package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.REF_AS_NON_NULL
import io.github.charlietap.chasm.compiler.backend.abi.REF_CAST
import io.github.charlietap.chasm.compiler.backend.abi.REF_EQ
import io.github.charlietap.chasm.compiler.backend.abi.REF_IS_NULL
import io.github.charlietap.chasm.compiler.backend.abi.REF_TEST
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.ext.nullReference

internal fun lowerReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> context.slotHelper(REF_IS_NULL, instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefAsNonNullS -> context.slotHelper(REF_AS_NON_NULL, instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefNullS -> {
            context.emit.constWide(Registers.W0, instruction.heapType.nullReference())
            context.store(instruction.destinationSlot, Registers.W0)
        }
        is ReferenceInstruction.RefFuncS -> {
            context.loadValue(Registers.W0, Resource.FunctionReference(instruction.function.index))
            context.store(instruction.destinationSlot, Registers.W0)
        }
        is ReferenceInstruction.RefEqSs -> {
            context.loadWide(Registers.W0, instruction.reference1Slot)
            context.loadWide(Registers.W1, instruction.reference2Slot)
            context.emit.invoke(REF_EQ, Registers.W0, Registers.W0 + 1, Registers.W1, Registers.W1 + 1)
            context.emit.moveResultWide(Registers.W0)
            context.store(instruction.destinationSlot, Registers.W0)
        }
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
