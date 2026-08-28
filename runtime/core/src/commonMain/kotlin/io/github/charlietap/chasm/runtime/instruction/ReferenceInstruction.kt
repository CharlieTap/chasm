package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

sealed interface ReferenceInstruction : LinkedInstruction {

    data class RefCastS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val typeTest: ReferenceTypeTest,
    ) : ReferenceInstruction

    data class RefEqSs(
        val reference1Slot: Int,
        val reference2Slot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction

    data class RefIsNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction

    data class RefAsNonNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction

    data class RefNullS(
        val reference: Long,
        val destinationSlot: Int,
    ) : ReferenceInstruction

    data class RefFuncS(
        val reference: Long,
        val destinationSlot: Int,
    ) : ReferenceInstruction

    data class RefTestS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val typeTest: ReferenceTypeTest,
    ) : ReferenceInstruction
}
