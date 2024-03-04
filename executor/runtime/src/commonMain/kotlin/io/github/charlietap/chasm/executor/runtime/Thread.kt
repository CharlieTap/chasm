package io.github.charlietap.chasm.executor.runtime

import io.github.charlietap.chasm.ast.instruction.Instruction

data class Thread(
    val frame: Stack.Entry.ActivationFrame,
    val instructions: List<Instruction>,
)
