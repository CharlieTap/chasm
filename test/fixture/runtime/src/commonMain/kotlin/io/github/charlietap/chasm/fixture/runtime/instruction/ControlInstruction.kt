package io.github.charlietap.chasm.fixture.runtime.instruction

import io.github.charlietap.chasm.runtime.instruction.ControlInstruction

fun controlRuntimeInstruction(): ControlInstruction = unreachableRuntimeInstruction()

fun unreachableRuntimeInstruction() = ControlInstruction.Unreachable
