package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.GlobalInstance

sealed interface VariableInstruction : LinkedInstruction {

    data class GlobalGetS(
        val global: GlobalInstance,
        val destinationSlot: Int,
    ) : VariableInstruction

    data class GlobalSetI(
        val value: Long,
        val global: GlobalInstance,
    ) : VariableInstruction

    data class GlobalSetS(
        val sourceSlot: Int,
        val global: GlobalInstance,
    ) : VariableInstruction
}
