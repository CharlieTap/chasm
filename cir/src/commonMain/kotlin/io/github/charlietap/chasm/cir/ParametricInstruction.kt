package io.github.charlietap.chasm.cir

sealed class ParametricInstruction : Instruction() {

    @CirTag("parametric.select.iii")
    data class SelectIii(
        val condition: Long,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 654
        }
    }

    @CirTag("parametric.select.iis")
    data class SelectIis(
        val condition: Long,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 655
        }
    }

    @CirTag("parametric.select.isi")
    data class SelectIsi(
        val condition: Long,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 656
        }
    }

    @CirTag("parametric.select.iss")
    data class SelectIss(
        val condition: Long,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 657
        }
    }

    @CirTag("parametric.select.sii")
    data class SelectSii(
        val conditionSlot: Int,
        val val1: Long,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 658
        }
    }

    @CirTag("parametric.select.sis")
    data class SelectSis(
        val conditionSlot: Int,
        val val1: Long,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 659
        }
    }

    @CirTag("parametric.select.ssi")
    data class SelectSsi(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2: Long,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 660
        }
    }

    @CirTag("parametric.select.sss")
    data class SelectSss(
        val conditionSlot: Int,
        val val1Slot: Int,
        val val2Slot: Int,
        val destinationSlot: Int,
    ) : ParametricInstruction() {
        init {
            opcode = 661
        }
    }
}
