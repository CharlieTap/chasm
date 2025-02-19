package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.instance.GlobalInstance

sealed interface FusedVariableInstruction : LinkedInstruction {

    data class GlobalSet(
        val operand: LoadOp,
        val global: GlobalInstance,
    ) : VariableInstruction

    data class LocalSet(
        val operand: LoadOp,
        val localIdx: Int,
    ) : VariableInstruction

    data class LocalTee(
        val operand: LoadOp,
        val localIdx: Int,
    ) : VariableInstruction
}
