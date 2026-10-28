package io.github.charlietap.chasm.cir

sealed class AdminInstruction : Instruction() {

    @CirTag("admin.end_function")
    data class EndFunction(
        val resultCount: Int,
        val activationHeaderSlot: Int,
    ) : AdminInstruction() {
        init {
            opcode = 684
        }
    }

    @CirTag("admin.checkpoint")
    data object Checkpoint : AdminInstruction() {
        init {
            opcode = 685
        }
    }

    @CirTag("admin.copy_slot")
    data class CopySlot(
        val sourceSlot: Int,
        val destinationSlot: Int,
    ) : AdminInstruction() {
        init {
            opcode = 686
        }
    }

    @CirTag("admin.copy_slots")
    data class CopySlots(
        val sourceSlots: IntArray,
        val destinationSlots: IntArray,
    ) : AdminInstruction() {
        init {
            opcode = 687
        }

        override fun equals(
            other: Any?,
        ): Boolean =
            this === other ||
                other is CopySlots &&
                sourceSlots.contentEquals(other.sourceSlots) &&
                destinationSlots.contentEquals(other.destinationSlots)

        override fun hashCode(): Int {
            var result = sourceSlots.contentHashCode()
            result = 31 * result + destinationSlots.contentHashCode()
            return result
        }
    }

    @CirTag("admin.copy_slot_sequence")
    data class CopySlotSequence(
        val sourceSlots: IntArray,
        val destinationSlots: IntArray,
    ) : AdminInstruction() {
        init {
            opcode = 688
        }

        override fun equals(
            other: Any?,
        ): Boolean =
            this === other ||
                other is CopySlotSequence &&
                sourceSlots.contentEquals(other.sourceSlots) &&
                destinationSlots.contentEquals(other.destinationSlots)

        override fun hashCode(): Int {
            var result = sourceSlots.contentHashCode()
            result = 31 * result + destinationSlots.contentHashCode()
            return result
        }
    }

    @CirTag("admin.jump")
    data class Jump(
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 689
        }
    }

    @CirTag("admin.jump_copies")
    data class JumpCopies(
        val operands: OperandTransfer,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 690
        }
    }

    @CirTag("admin.jump_if.i")
    data class JumpIfI(
        val operand: Long,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 691
        }
    }

    @CirTag("admin.jump_if.s")
    data class JumpIfS(
        val operandSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 692
        }
    }

    @CirTag("admin.jump_if_zero.i")
    data class JumpIfZeroI(
        val operand: Long,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 693
        }
    }

    @CirTag("admin.jump_if_zero.s")
    data class JumpIfZeroS(
        val operandSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 694
        }
    }

    @CirTag("admin.jump_if_copy.i")
    data class JumpIfCopyI(
        val operand: Long,
        val sourceSlot: Int,
        val destinationSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 695
        }
    }

    @CirTag("admin.jump_if_copy.s")
    data class JumpIfCopyS(
        val operandSlot: Int,
        val sourceSlot: Int,
        val destinationSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 696
        }
    }

    @CirTag("admin.jump_if_condition")
    data class JumpIfCondition(
        val condition: NumericCondition,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 697
        }
    }

    @CirTag("admin.jump_if_condition_mismatch")
    data class JumpIfConditionMismatch(
        val condition: NumericCondition,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 698
        }
    }

    @CirTag("admin.jump_table.s")
    data class JumpTableS(
        val operandSlot: Int,
        val targets: IntArray,
    ) : AdminInstruction() {
        init {
            opcode = 699
        }

        override fun equals(
            other: Any?,
        ): Boolean =
            this === other ||
                other is JumpTableS &&
                operandSlot == other.operandSlot &&
                targets.contentEquals(other.targets)

        override fun hashCode(): Int {
            var result = operandSlot.hashCode()
            result = 31 * result + targets.contentHashCode()
            return result
        }
    }

    @CirTag("admin.jump_on_null.i")
    data class JumpOnNullI(
        val operand: Long,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 700
        }
    }

    @CirTag("admin.jump_on_null.s")
    data class JumpOnNullS(
        val operandSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 701
        }
    }

    @CirTag("admin.jump_on_non_null.i")
    data class JumpOnNonNullI(
        val operand: Long,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 702
        }
    }

    @CirTag("admin.jump_on_non_null.s")
    data class JumpOnNonNullS(
        val operandSlot: Int,
        val target: InstructionIndex,
    ) : AdminInstruction() {
        init {
            opcode = 703
        }
    }

    @CirTag("admin.jump_on_cast.i")
    data class JumpOnCastI(
        val operand: Long,
        val target: InstructionIndex,
        val typeTest: ReferenceType,
    ) : AdminInstruction() {
        init {
            opcode = 704
        }
    }

    @CirTag("admin.jump_on_cast.s")
    data class JumpOnCastS(
        val operandSlot: Int,
        val target: InstructionIndex,
        val typeTest: ReferenceType,
    ) : AdminInstruction() {
        init {
            opcode = 705
        }
    }

    @CirTag("admin.jump_on_cast_fail.i")
    data class JumpOnCastFailI(
        val operand: Long,
        val target: InstructionIndex,
        val typeTest: ReferenceType,
    ) : AdminInstruction() {
        init {
            opcode = 706
        }
    }

    @CirTag("admin.jump_on_cast_fail.s")
    data class JumpOnCastFailS(
        val operandSlot: Int,
        val target: InstructionIndex,
        val typeTest: ReferenceType,
    ) : AdminInstruction() {
        init {
            opcode = 707
        }
    }
}
