package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.type.ReferenceType

sealed interface FusedReferenceInstruction : LinkedInstruction {

    data class RefEq(
        val reference1: LoadOp,
        val reference2: LoadOp,
        val destination: StoreOp,
    ) : FusedReferenceInstruction

    data class RefIsNull(
        val value: LoadOp,
        val destination: StoreOp,
    ) : FusedReferenceInstruction

    data class RefNull(
        val destination: StoreOp,
        val reference: Long,
    ) : FusedReferenceInstruction

    data class RefTest(
        val reference: LoadOp,
        val destination: StoreOp,
        val referenceType: ReferenceType,
    ) : FusedReferenceInstruction
}
