package io.github.charlietap.chasm.ir.instruction

sealed interface FusedNumericInstruction : NumericInstruction {

    data class I32Add(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class I32Sub(
        val left: FusedOperand,
        val right: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction

    data class F32Abs(
        val operand: FusedOperand,
        val destination: FusedDestination,
    ) : FusedNumericInstruction
}
