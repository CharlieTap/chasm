package io.github.charlietap.chasm.validator.context

import io.github.charlietap.chasm.ast.instruction.Instruction

internal interface InstructionContext {
    var instruction: Instruction?
}

internal data class InstructionContextImpl(
    override var instruction: Instruction? = null,
) : InstructionContext
