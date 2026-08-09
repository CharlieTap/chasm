package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun adminInstruction(): AdminInstruction = endFunctionAdminInstruction()

fun copySlotsAdminInstruction(
    sourceSlots: List<Int> = [],
    destinationSlots: List<Int> = [],
) = AdminInstruction.CopySlots(
    sourceSlots = sourceSlots,
    destinationSlots = destinationSlots,
)

fun endFunctionAdminInstruction() = AdminInstruction.EndFunction
