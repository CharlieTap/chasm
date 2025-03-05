package io.github.charlietap.chasm.fixture.executor.runtime.instruction

import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.label
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack

fun adminInstruction(): AdminInstruction = frameAdminInstruction()

fun frameAdminInstruction(
    frame: ActivationFrame = frame(),
) = AdminInstruction.Frame(frame)

fun labelAdminInstruction(
    label: ControlStack.Entry.Label = label(),
) = AdminInstruction.Label(label)
