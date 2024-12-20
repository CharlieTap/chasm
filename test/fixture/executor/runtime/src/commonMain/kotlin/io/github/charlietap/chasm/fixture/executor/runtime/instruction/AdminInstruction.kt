package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.fixture.executor.runtime.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame

fun adminInstruction(): AdminInstruction = frameAdminInstruction()

fun frameAdminInstruction(
    frame: ActivationFrame = frame(),
) = AdminInstruction.Frame(frame)

fun labelAdminInstruction(
    label: Stack.Entry.Label = label(),
) = AdminInstruction.Label(label)
