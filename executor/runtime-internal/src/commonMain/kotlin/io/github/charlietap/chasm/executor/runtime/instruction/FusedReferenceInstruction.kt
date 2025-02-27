package io.github.charlietap.chasm.executor.runtime.instruction

sealed interface FusedReferenceInstruction : LinkedInstruction {

    data class RefNull(
        val destination: StoreOp,
        val reference: Long,
    ) : FusedReferenceInstruction
}
