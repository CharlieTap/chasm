package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.GlobalInstance

sealed interface FusedVariableInstruction : LinkedInstruction {

    data class GlobalSet(
        val operand: LoadOp,
        val global: GlobalInstance,
    ) : FusedVariableInstruction

    data class LocalSet(
        val operand: LoadOp,
        val localIdx: Int,
    ) : FusedVariableInstruction

    data class LocalTee(
        val operand: LoadOp,
        val localIdx: Int,
    ) : FusedVariableInstruction
}
