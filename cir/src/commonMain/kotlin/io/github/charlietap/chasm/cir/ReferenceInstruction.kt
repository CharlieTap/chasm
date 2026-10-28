package io.github.charlietap.chasm.cir

sealed class ReferenceInstruction : Instruction() {

    @CirTag("reference.ref_cast")
    data class RefCastS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val typeTest: ReferenceType,
    ) : ReferenceInstruction() {
        init {
            opcode = 665
        }
    }

    @CirTag("reference.ref_eq")
    data class RefEqSs(
        val reference1Slot: Int,
        val reference2Slot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction() {
        init {
            opcode = 666
        }
    }

    @CirTag("reference.ref_is_null")
    data class RefIsNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction() {
        init {
            opcode = 667
        }
    }

    @CirTag("reference.ref_as_non_null")
    data class RefAsNonNullS(
        val valueSlot: Int,
        val destinationSlot: Int,
    ) : ReferenceInstruction() {
        init {
            opcode = 668
        }
    }

    @CirTag("reference.ref_null")
    data class RefNullS(
        val heapType: HeapType,
        val destinationSlot: Int,
    ) : ReferenceInstruction() {
        init {
            opcode = 669
        }
    }

    @CirTag("reference.ref_func")
    data class RefFuncS(
        val function: FunctionIndex,
        val destinationSlot: Int,
    ) : ReferenceInstruction() {
        init {
            opcode = 670
        }
    }

    @CirTag("reference.ref_test")
    data class RefTestS(
        val referenceSlot: Int,
        val destinationSlot: Int,
        val typeTest: ReferenceType,
    ) : ReferenceInstruction() {
        init {
            opcode = 671
        }
    }
}
