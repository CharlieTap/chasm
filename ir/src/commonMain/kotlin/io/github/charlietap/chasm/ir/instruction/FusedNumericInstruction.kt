package io.github.charlietap.chasm.ir.instruction

sealed interface FusedNumericInstruction : NumericInstruction {

    data class I32Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction
}
