package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun adminInstruction(): AdminInstruction = endFunctionAdminInstruction()

fun copySlotsAdminInstruction(
    sourceSlots: List<Int> = [],
    destinationSlots: List<Int> = [],
) = AdminInstruction.CopySlots(
    sourceSlots = sourceSlots.toIntArray(),
    destinationSlots = destinationSlots.toIntArray(),
)

fun endFunctionAdminInstruction() = AdminInstruction.EndFunction
