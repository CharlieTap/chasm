package io.github.charlietap.chasm.cir

sealed class VariableInstruction : Instruction() {

    @CirTag("variable.global_get")
    data class GlobalGetS(
        val global: GlobalIndex,
        val destinationSlot: Int,
    ) : VariableInstruction() {
        init {
            opcode = 662
        }
    }

    @CirTag("variable.global_set.i")
    data class GlobalSetI(
        val value: Long,
        val global: GlobalIndex,
    ) : VariableInstruction() {
        init {
            opcode = 663
        }
    }

    @CirTag("variable.global_set.s")
    data class GlobalSetS(
        val sourceSlot: Int,
        val global: GlobalIndex,
    ) : VariableInstruction() {
        init {
            opcode = 664
        }
    }
}
