package io.github.charlietap.chasm.runtime.instruction

sealed interface FusedParametricInstruction : LinkedInstruction {

    data class Select(
        val const: LoadOp,
        val val1: LoadOp,
        val val2: LoadOp,
        val destination: StoreOp,
    ) : FusedParametricInstruction
}
