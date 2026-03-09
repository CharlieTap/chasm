package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface FusedReferenceInstruction : LinkedInstruction {

    data class RefCastS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val referenceType: ReferenceType,
    ) : FusedReferenceInstruction

    data class RefEqSs(
        val reference1Slot: Int,
        val reference2Slot: Int,
        val destinationSlot: Int,
    ) : FusedReferenceInstruction

    data class RefIsNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedReferenceInstruction

    data class RefAsNonNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : FusedReferenceInstruction

    data class RefNullS(
        val reference: Long,
        val destinationSlot: Int,
    ) : FusedReferenceInstruction

    data class RefFuncS(
        val reference: Long,
        val destinationSlot: Int,
    ) : FusedReferenceInstruction

    data class RefTestS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val referenceType: ReferenceType,
    ) : FusedReferenceInstruction
}
