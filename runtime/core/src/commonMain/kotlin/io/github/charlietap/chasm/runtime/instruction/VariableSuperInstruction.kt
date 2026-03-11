package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.runtime.instance.GlobalInstance

sealed interface VariableSuperInstruction : LinkedInstruction {

    data class GlobalGetS(
        val global: GlobalInstance,
        val destinationSlot: Int,
    ) : VariableSuperInstruction

    data class GlobalSetI(
        val value: Long,
        val global: GlobalInstance,
    ) : VariableSuperInstruction

    data class GlobalSetS(
        val sourceSlot: Int,
        val global: GlobalInstance,
    ) : VariableSuperInstruction

    data class LocalSetI(
        val value: Long,
        val localSlot: Int,
    ) : VariableSuperInstruction

    data class LocalSetS(
        val sourceSlot: Int,
        val localSlot: Int,
    ) : VariableSuperInstruction
}
