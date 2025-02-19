package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index.GlobalIndex
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

sealed interface FusedVariableInstruction : Instruction {

    data class LocalSet(
        val operand: FusedOperand,
        val localIdx: LocalIndex,
    ) : FusedVariableInstruction

    data class LocalTee(
        val operand: FusedOperand,
        val localIdx: LocalIndex,
    ) : FusedVariableInstruction

    data class GlobalSet(
        val operand: FusedOperand,
        val globalIdx: GlobalIndex,
    ) : FusedVariableInstruction
}
