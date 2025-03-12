package io.github.charlietap.chasm.ir.instruction

sealed interface AdminInstruction : Instruction {

    data object EndBlock : AdminInstruction

    data object EndFunction : AdminInstruction
}
