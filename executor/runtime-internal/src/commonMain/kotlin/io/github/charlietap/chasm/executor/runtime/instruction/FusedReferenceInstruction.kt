package io.github.charlietap.chasm.executor.runtime.instruction

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
}
