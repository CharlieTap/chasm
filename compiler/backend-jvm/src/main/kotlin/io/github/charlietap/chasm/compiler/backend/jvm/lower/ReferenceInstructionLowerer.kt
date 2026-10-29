package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.REF_AS_NON_NULL
import io.github.charlietap.chasm.compiler.backend.abi.REF_CAST
import io.github.charlietap.chasm.compiler.backend.abi.REF_EQ
import io.github.charlietap.chasm.compiler.backend.abi.REF_IS_NULL
import io.github.charlietap.chasm.compiler.backend.abi.REF_TEST
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.ext.nullReference
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot

internal fun lowerReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> context.slotHelper(REF_IS_NULL, instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefAsNonNullS -> context.slotHelper(REF_AS_NON_NULL, instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefNullS ->
            context.copyToSlot(instruction.destinationSlot, Operand.Immediate(instruction.heapType.nullReference()))
        is ReferenceInstruction.RefFuncS -> {
            context.pushResource(Resource.FunctionReference(instruction.function.index))
            context.storeLong(instruction.destinationSlot)
        }
        is ReferenceInstruction.RefEqSs -> {
            context.pushLong(slot(instruction.reference1Slot))
            context.pushLong(slot(instruction.reference2Slot))
            context.emit.invokeStatic(REF_EQ)
            context.storeLong(instruction.destinationSlot)
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
    pushLong(slot(valueSlot))
    emit.invokeStatic(method)
    storeLong(destination)
}

private fun LowerContext.typeTest(
    method: MethodRef,
    referenceSlot: Int,
    type: ReferenceType,
    destination: Int,
) {
    pushContext()
    pushLong(slot(referenceSlot))
    pushResource(Resource.TypeTest(type))
    emit.invokeStatic(method)
    storeLong(destination)
}
