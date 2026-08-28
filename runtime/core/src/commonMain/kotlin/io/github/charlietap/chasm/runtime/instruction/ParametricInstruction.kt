package io.github.charlietap.chasm.runtime.instruction

sealed interface ParametricInstruction : LinkedInstruction {

    data class SelectIii(
        val condition: Long,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectIis(
        val condition: Long,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectIsi(
        val condition: Long,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectIss(
        val condition: Long,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectSii(
        val conditionSlot: Int,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectSis(
        val conditionSlot: Int,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectSsi(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction

    data class SelectSss(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction
}
