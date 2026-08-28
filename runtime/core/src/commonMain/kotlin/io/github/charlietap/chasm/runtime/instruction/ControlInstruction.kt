package io.github.charlietap.chasm.runtime.instruction

sealed interface ControlInstruction : LinkedInstruction {

    data object Unreachable : ControlInstruction
}
