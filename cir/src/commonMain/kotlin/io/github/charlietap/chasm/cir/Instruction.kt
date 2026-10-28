package io.github.charlietap.chasm.cir

sealed class Instruction {
    var opcode: Int = UNSET_OPCODE
        protected set
}

private const val UNSET_OPCODE = -1
