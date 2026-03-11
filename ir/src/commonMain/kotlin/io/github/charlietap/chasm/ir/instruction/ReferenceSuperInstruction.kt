package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ReferenceType

sealed interface ReferenceSuperInstruction : Instruction {

    data class RefCast(
        val reference: FusedOperand,
        val destination: FusedDestination,
        val referenceType: ReferenceType,
    ) : ReferenceSuperInstruction

    data class RefEq(
        val reference1: FusedOperand,
        val reference2: FusedOperand,
        val destination: FusedDestination,
    ) : ReferenceSuperInstruction

    data class RefIsNull(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : ReferenceSuperInstruction

    data class RefAsNonNull(
        val value: FusedOperand,
        val destination: FusedDestination,
    ) : ReferenceSuperInstruction

    data class RefNull(
        val destination: FusedDestination,
        val type: HeapType,
    ) : ReferenceSuperInstruction

    data class RefFunc(
        val destination: FusedDestination,
        val funcIdx: io.github.charlietap.chasm.ir.module.Index.FunctionIndex,
    ) : ReferenceSuperInstruction

    data class RefTest(
        val reference: FusedOperand,
        val destination: FusedDestination,
        val referenceType: ReferenceType,
    ) : ReferenceSuperInstruction
}
