package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.HeapType

sealed interface FusedReferenceInstruction : Instruction {

    data class RefIsNull(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : FusedReferenceInstruction

    data class RefNull(
        val destination: FusedDestination,
        val type: HeapType,
    ) : FusedReferenceInstruction
}
