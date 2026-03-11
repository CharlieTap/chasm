package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType

fun fusedControlInstruction(): ControlSuperInstruction = fusedIf()

fun fusedCall(
    operands: List<FusedOperand> = emptyList(),
    functionIndex: Index.FunctionIndex = functionIndex(),
    resultSlots: List<Int> = emptyList(),
    callFrameSlot: Int = 0,
) = ControlSuperInstruction.Call(
    operands = operands,
    functionIndex = functionIndex,
    resultSlots = resultSlots,
    callFrameSlot = callFrameSlot,
)

fun fusedReturnCall(
    operands: List<FusedOperand> = emptyList(),
    functionIndex: Index.FunctionIndex = functionIndex(),
) = ControlSuperInstruction.ReturnCall(
    operands = operands,
    functionIndex = functionIndex,
)

fun fusedCallIndirect(
    elementIndex: FusedOperand = fusedOperand(),
    operands: List<FusedOperand> = emptyList(),
    typeIndex: Index.TypeIndex = typeIndex(),
    tableIndex: Index.TableIndex = tableIndex(),
    resultSlots: List<Int> = emptyList(),
    callFrameSlot: Int = 0,
) = ControlSuperInstruction.CallIndirect(
    elementIndex = elementIndex,
    operands = operands,
    typeIndex = typeIndex,
    tableIndex = tableIndex,
    resultSlots = resultSlots,
    callFrameSlot = callFrameSlot,
)

fun fusedCallRef(
    functionReference: FusedOperand = fusedOperand(),
    operands: List<FusedOperand> = emptyList(),
    typeIndex: Index.TypeIndex = typeIndex(),
    resultSlots: List<Int> = emptyList(),
    callFrameSlot: Int = 0,
) = ControlSuperInstruction.CallRef(
    functionReference = functionReference,
    operands = operands,
    typeIndex = typeIndex,
    resultSlots = resultSlots,
    callFrameSlot = callFrameSlot,
)

fun fusedReturnCallIndirect(
    elementIndex: FusedOperand = fusedOperand(),
    operands: List<FusedOperand> = emptyList(),
    typeIndex: Index.TypeIndex = typeIndex(),
    tableIndex: Index.TableIndex = tableIndex(),
) = ControlSuperInstruction.ReturnCallIndirect(
    elementIndex = elementIndex,
    operands = operands,
    typeIndex = typeIndex,
    tableIndex = tableIndex,
)

fun fusedReturnCallRef(
    functionReference: FusedOperand = fusedOperand(),
    operands: List<FusedOperand> = emptyList(),
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlSuperInstruction.ReturnCallRef(
    functionReference = functionReference,
    operands = operands,
    typeIndex = typeIndex,
)

fun fusedBrIf(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    takenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrIf(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = takenInstructions,
)

fun fusedBrIf(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = fusedBrIf(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = slotCopyInstructions(sourceSlots, destinationSlots),
)

fun fusedBrTable(
    operand: FusedOperand = fusedOperand(),
    labelIndices: List<Index.LabelIndex> = emptyList(),
    defaultLabelIndex: Index.LabelIndex = labelIndex(),
    takenInstructions: List<List<Instruction>> = emptyList(),
    defaultTakenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrTable(
    operand = operand,
    labelIndices = labelIndices,
    defaultLabelIndex = defaultLabelIndex,
    takenInstructions = takenInstructions,
    defaultTakenInstructions = defaultTakenInstructions,
)

fun fusedBrTable(
    operand: FusedOperand = fusedOperand(),
    labelIndices: List<Index.LabelIndex> = emptyList(),
    defaultLabelIndex: Index.LabelIndex = labelIndex(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<List<Int>> = emptyList(),
    defaultDestinationSlots: List<Int> = emptyList(),
) = fusedBrTable(
    operand = operand,
    labelIndices = labelIndices,
    defaultLabelIndex = defaultLabelIndex,
    takenInstructions = destinationSlots.map { slots ->
        slotCopyInstructions(sourceSlots, slots)
    },
    defaultTakenInstructions = slotCopyInstructions(sourceSlots, defaultDestinationSlots),
)

fun fusedBrOnNull(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    takenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrOnNull(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = takenInstructions,
)

fun fusedBrOnNull(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = fusedBrOnNull(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = slotCopyInstructions(sourceSlots, destinationSlots),
)

fun fusedBrOnNonNull(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    takenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrOnNonNull(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = takenInstructions,
)

fun fusedBrOnNonNull(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = fusedBrOnNonNull(
    operand = operand,
    labelIndex = labelIndex,
    takenInstructions = slotCopyInstructions(sourceSlots, destinationSlots),
)

fun fusedBrOnCast(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
    takenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrOnCast(
    operand = operand,
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
    takenInstructions = takenInstructions,
)

fun fusedBrOnCast(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = fusedBrOnCast(
    operand = operand,
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
    takenInstructions = slotCopyInstructions(sourceSlots, destinationSlots),
)

fun fusedBrOnCastFail(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
    takenInstructions: List<Instruction> = emptyList(),
) = ControlSuperInstruction.BrOnCastFail(
    operand = operand,
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
    takenInstructions = takenInstructions,
)

fun fusedBrOnCastFail(
    operand: FusedOperand = fusedOperand(),
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
    sourceSlots: List<Int> = emptyList(),
    destinationSlots: List<Int> = emptyList(),
) = fusedBrOnCastFail(
    operand = operand,
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
    takenInstructions = slotCopyInstructions(sourceSlots, destinationSlots),
)

fun fusedIf(
    operand: FusedOperand = fusedOperand(),
    blockType: BlockType = blockType(),
    thenInstructions: List<Instruction> = emptyList(),
    elseInstructions: List<Instruction>? = null,
) = ControlSuperInstruction.If(
    operand = operand,
    blockType = blockType,
    thenInstructions = thenInstructions,
    elseInstructions = elseInstructions,
)

private fun slotCopyInstructions(
    sourceSlots: List<Int>,
    destinationSlots: List<Int>,
): List<Instruction> = if (sourceSlots == destinationSlots) {
    emptyList()
} else {
    listOf(
        AdminInstruction.CopySlots(
            sourceSlots = sourceSlots,
            destinationSlots = destinationSlots,
        ),
    )
}
