package io.github.charlietap.chasm.executor.runtime.instruction

sealed interface FusedReferenceInstruction : LinkedInstruction {

    data class RefIsNull(
        val value: LoadOp,
        val destination: StoreOp,
    ) : FusedReferenceInstruction

    data class RefNull(
        val destination: StoreOp,
        val reference: Long,
    ) : FusedReferenceInstruction
}
