package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType

sealed interface ControlSuperInstruction : Instruction {

    data class BrIf(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class BrTable(
        val operand: FusedOperand,
        val labelIndices: List<Index.LabelIndex>,
        val defaultLabelIndex: Index.LabelIndex,
        val takenInstructions: List<List<Instruction>> = emptyList(),
        val defaultTakenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class BrOnNull(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class BrOnNonNull(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class BrOnCast(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class BrOnCastFail(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : ControlSuperInstruction

    data class Call(
        val operands: List<FusedOperand>,
        val functionIndex: Index.FunctionIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : ControlSuperInstruction

    data class ReturnCall(
        val operands: List<FusedOperand>,
        val functionIndex: Index.FunctionIndex,
    ) : ControlSuperInstruction

    data class CallIndirect(
        val elementIndex: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : ControlSuperInstruction

    data class CallRef(
        val functionReference: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : ControlSuperInstruction

    data class ReturnCallIndirect(
        val elementIndex: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
    ) : ControlSuperInstruction

    data class ReturnCallRef(
        val functionReference: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
    ) : ControlSuperInstruction

    data class Throw(
        val tagIndex: Index.TagIndex,
        val payloads: List<FusedOperand>,
    ) : ControlSuperInstruction

    data class ThrowRef(
        val exceptionReference: FusedOperand,
    ) : ControlSuperInstruction

    data class If(
        val operand: FusedOperand,
        val blockType: BlockType,
        val thenInstructions: List<Instruction>,
        val elseInstructions: List<Instruction>?,
    ) : ControlSuperInstruction
}
