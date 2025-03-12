package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.AdminInstruction

fun adminInstruction(): AdminInstruction = endFunctionAdminInstruction()

fun endBlockAdminInstruction() = AdminInstruction.EndBlock

fun endFunctionAdminInstruction() = AdminInstruction.EndFunction
