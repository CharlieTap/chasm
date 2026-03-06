package io.github.charlietap.chasm.runtime.instruction

sealed interface FusedParametricInstruction : LinkedInstruction {

    data class SelectIii(
        val condition: Long,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectIis(
        val condition: Long,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectIsi(
        val condition: Long,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectIss(
        val condition: Long,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectSii(
        val conditionSlot: Int,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectSis(
        val conditionSlot: Int,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectSsi(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : FusedParametricInstruction

    data class SelectSss(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : FusedParametricInstruction
}
