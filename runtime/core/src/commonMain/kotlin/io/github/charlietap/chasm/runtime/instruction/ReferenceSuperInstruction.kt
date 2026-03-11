package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface ReferenceSuperInstruction : LinkedInstruction {

    data class RefCastS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val referenceType: ReferenceType,
    ) : ReferenceSuperInstruction

    data class RefEqSs(
        val reference1Slot: Int,
        val reference2Slot: Int,
        val destinationSlot: Int,
    ) : ReferenceSuperInstruction

    data class RefIsNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceSuperInstruction

    data class RefAsNonNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceSuperInstruction

    data class RefNullS(
        val reference: Long,
        val destinationSlot: Int,
    ) : ReferenceSuperInstruction

    data class RefFuncS(
        val reference: Long,
        val destinationSlot: Int,
    ) : ReferenceSuperInstruction

    data class RefTestS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val referenceType: ReferenceType,
    ) : ReferenceSuperInstruction
}
