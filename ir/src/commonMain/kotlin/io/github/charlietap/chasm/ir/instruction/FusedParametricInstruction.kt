package io.github.charlietap.chasm.ir.instruction

sealed interface FusedParametricInstruction : Instruction {

    data class Select(
        val const: FusedOperand,
        val val1: FusedOperand,
        val val2: FusedOperand,
        val destination: FusedDestination,
    ) : FusedParametricInstruction
}
