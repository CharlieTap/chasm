package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

data class ActivationFrame(
    val arity: Int,
    val depths: StackDepths,
    val instance: ModuleInstance,
    val previousInstructions: Array<DispatchableInstruction>,
    val previousInstructionPointer: InstructionPointer,
    val previousFramePointer: Int,
)
