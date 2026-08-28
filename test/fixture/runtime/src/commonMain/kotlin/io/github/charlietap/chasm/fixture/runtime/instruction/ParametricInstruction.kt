package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.ParametricInstruction

fun parametricRuntimeInstruction(): ParametricInstruction = selectIiiRuntimeInstruction()

fun selectIiiRuntimeInstruction(
    condition: Long = 0L,
    val1: Long = 0L,
    val2: Long = 0L,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectIii(
    condition = condition,
    val1 = val1,
    val2 = val2,
    destinationSlot = destinationSlot,
)

fun selectIisRuntimeInstruction(
    condition: Long = 0L,
    val1: Long = 0L,
    val2Slot: Int = 0,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectIis(
    condition = condition,
    val1 = val1,
    val2Slot = val2Slot,
    destinationSlot = destinationSlot,
)

fun selectIsiRuntimeInstruction(
    condition: Long = 0L,
    val1Slot: Int = 0,
    val2: Long = 0L,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectIsi(
    condition = condition,
    val1Slot = val1Slot,
    val2 = val2,
    destinationSlot = destinationSlot,
)

fun selectIssRuntimeInstruction(
    condition: Long = 0L,
    val1Slot: Int = 0,
    val2Slot: Int = 0,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectIss(
    condition = condition,
    val1Slot = val1Slot,
    val2Slot = val2Slot,
    destinationSlot = destinationSlot,
)

fun selectSiiRuntimeInstruction(
    conditionSlot: Int = 0,
    val1: Long = 0L,
    val2: Long = 0L,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectSii(
    conditionSlot = conditionSlot,
    val1 = val1,
    val2 = val2,
    destinationSlot = destinationSlot,
)

fun selectSisRuntimeInstruction(
    conditionSlot: Int = 0,
    val1: Long = 0L,
    val2Slot: Int = 0,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectSis(
    conditionSlot = conditionSlot,
    val1 = val1,
    val2Slot = val2Slot,
    destinationSlot = destinationSlot,
)

fun selectSsiRuntimeInstruction(
    conditionSlot: Int = 0,
    val1Slot: Int = 0,
    val2: Long = 0L,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectSsi(
    conditionSlot = conditionSlot,
    val1Slot = val1Slot,
    val2 = val2,
    destinationSlot = destinationSlot,
)

fun selectSssRuntimeInstruction(
    conditionSlot: Int = 0,
    val1Slot: Int = 0,
    val2Slot: Int = 0,
    destinationSlot: Int = 0,
) = ParametricInstruction.SelectSss(
    conditionSlot = conditionSlot,
    val1Slot = val1Slot,
    val2Slot = val2Slot,
    destinationSlot = destinationSlot,
)
