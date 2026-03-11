package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index.GlobalIndex
import io.github.charlietap.chasm.ir.module.Index.LocalIndex

sealed interface VariableSuperInstruction : Instruction {

    data class GlobalGet(
        val globalIdx: GlobalIndex,
        val destination: FusedDestination,
    ) : VariableSuperInstruction

    data class LocalSet(
        val operand: FusedOperand,
        val localIdx: LocalIndex,
    ) : VariableSuperInstruction

    data class LocalTee(
        val operand: FusedOperand,
        val localIdx: LocalIndex,
    ) : VariableSuperInstruction

    data class GlobalSet(
        val operand: FusedOperand,
        val globalIdx: GlobalIndex,
    ) : VariableSuperInstruction
}
