package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.instruction.Instruction

internal interface InstructionContext {
    val instruction: Instruction?
}

internal data class InstructionContextImpl(
    override val instruction: Instruction? = null,
) : InstructionContext
