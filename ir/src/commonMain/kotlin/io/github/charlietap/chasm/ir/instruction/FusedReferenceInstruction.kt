package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.HeapType

sealed interface FusedReferenceInstruction : Instruction {

    data class RefNull(
        val destination: FusedDestination,
        val type: HeapType,
    ) : FusedReferenceInstruction
}
