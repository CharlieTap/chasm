package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.GlobalInstance

sealed interface FusedVariableInstruction : LinkedInstruction {

    data class GlobalGetS(
        val global: GlobalInstance,
        val destinationSlot: Int,
    ) : FusedVariableInstruction

    data class GlobalSetI(
        val value: Long,
        val global: GlobalInstance,
    ) : FusedVariableInstruction

    data class GlobalSetS(
        val sourceSlot: Int,
        val global: GlobalInstance,
    ) : FusedVariableInstruction

    data class LocalSetI(
        val value: Long,
        val localSlot: Int,
    ) : FusedVariableInstruction

    data class LocalSetS(
        val sourceSlot: Int,
        val localSlot: Int,
    ) : FusedVariableInstruction
}
