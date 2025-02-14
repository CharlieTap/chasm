package io.github.charlietap.chasm.executor.runtime.instruction

sealed interface FusedNumericInstruction : LinkedInstruction {

    data class I32Add(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class I32Sub(
        val left: LoadOp,
        val right: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction

    data class F32Abs(
        val operand: LoadOp,
        val destination: StoreOp,
    ) : FusedNumericInstruction
}
