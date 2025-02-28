package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

sealed interface FusedReferenceInstruction : Instruction {

    data class RefEq(
        val reference1: FusedOperand,
        val reference2: FusedOperand,
        val destination: FusedDestination,
    ) : FusedReferenceInstruction

    data class RefIsNull(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedReferenceInstruction

    data class RefNull(
        val destination: FusedDestination,
        val type: HeapType,
    ) : FusedReferenceInstruction

    data class RefTest(
        val reference: FusedOperand,
        val destination: FusedDestination,
        val referenceType: ReferenceType,
    ) : FusedReferenceInstruction
}
