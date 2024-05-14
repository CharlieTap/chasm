package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.label

fun adminInstruction(): AdminInstruction = frameAdminInstruction()

fun frameAdminInstruction(
    frame: Stack.Entry.ActivationFrame = frame(),
) = AdminInstruction.Frame(frame)

fun labelAdminInstruction(
    label: Stack.Entry.Label = label(),
) = AdminInstruction.Label(label)
