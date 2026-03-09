package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType

sealed interface FusedControlInstruction : Instruction {

    data class BrIf(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class BrTable(
        val operand: FusedOperand,
        val labelIndices: List<Index.LabelIndex>,
        val defaultLabelIndex: Index.LabelIndex,
        val takenInstructions: List<List<Instruction>> = emptyList(),
        val defaultTakenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class BrOnNull(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class BrOnNonNull(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class BrOnCast(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class BrOnCastFail(
        val operand: FusedOperand,
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
        val takenInstructions: List<Instruction> = emptyList(),
    ) : FusedControlInstruction

    data class Call(
        val operands: List<FusedOperand>,
        val functionIndex: Index.FunctionIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : FusedControlInstruction

    data class ReturnCall(
        val operands: List<FusedOperand>,
        val functionIndex: Index.FunctionIndex,
    ) : FusedControlInstruction

    data class CallIndirect(
        val elementIndex: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : FusedControlInstruction

    data class CallRef(
        val functionReference: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val resultSlots: List<Int> = emptyList(),
        val callFrameSlot: Int = 0,
    ) : FusedControlInstruction

    data class ReturnCallIndirect(
        val elementIndex: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
    ) : FusedControlInstruction

    data class ReturnCallRef(
        val functionReference: FusedOperand,
        val operands: List<FusedOperand>,
        val typeIndex: Index.TypeIndex,
    ) : FusedControlInstruction

    data class Throw(
        val tagIndex: Index.TagIndex,
        val payloads: List<FusedOperand>,
    ) : FusedControlInstruction

    data class ThrowRef(
        val exceptionReference: FusedOperand,
    ) : FusedControlInstruction

    data class If(
        val operand: FusedOperand,
        val blockType: BlockType,
        val thenInstructions: List<Instruction>,
        val elseInstructions: List<Instruction>?,
    ) : FusedControlInstruction
}
