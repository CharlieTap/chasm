package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.fixture.module.functionIndex
import io.github.charlietap.chasm.fixture.module.labelIndex
import io.github.charlietap.chasm.fixture.module.tableIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.fixture.type.valueType

fun controlInstruction(): ControlInstruction = unreachableInstruction()

fun blockType(): ControlInstruction.BlockType = emptyBlockType()

fun emptyBlockType() = ControlInstruction.BlockType.Empty

fun valueBlockType(
    valueType: ValueType = valueType(),
) = ControlInstruction.BlockType.ValType(
    valueType = valueType,
)

fun signedTypeIndex(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.BlockType.SignedTypeIndex(
    typeIndex = typeIndex,
)

fun unreachableInstruction() = ControlInstruction.Unreachable

fun nopInstruction() = ControlInstruction.Nop

fun blockInstruction(
    blockType: ControlInstruction.BlockType = blockType(),
    instructions: List<Instruction> = emptyList(),
) = ControlInstruction.Block(
    blockType = blockType,
    instructions = instructions,
)

fun loopInstruction(
    blockType: ControlInstruction.BlockType = blockType(),
    instructions: List<Instruction> = emptyList(),
) = ControlInstruction.Loop(
    blockType = blockType,
    instructions = instructions,
)

fun ifInstruction(
    blockType: ControlInstruction.BlockType = blockType(),
    thenInstructions: List<Instruction> = emptyList(),
    elseInstructions: List<Instruction>? = null,
) = ControlInstruction.If(
    blockType = blockType,
    thenInstructions = thenInstructions,
    elseInstructions = elseInstructions,
)

fun brInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.Br(
    labelIndex = labelIndex,
)

fun brIfInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrIf(
    labelIndex = labelIndex,
)

fun brTableInstruction(
    labelIndices: List<Index.LabelIndex> = emptyList(),
    defaultLabelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrTable(
    labelIndices = labelIndices,
    defaultLabelIndex = defaultLabelIndex,
)

fun brOnNullInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrOnNull(
    labelIndex = labelIndex,
)

fun brOnNonNullInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.BrOnNonNull(
    labelIndex = labelIndex,
)

fun brOnCastInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = ControlInstruction.BrOnCast(
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun brOnCastFailInstruction(
    labelIndex: Index.LabelIndex = labelIndex(),
    srcReferenceType: ReferenceType = referenceType(),
    dstReferenceType: ReferenceType = referenceType(),
) = ControlInstruction.BrOnCastFail(
    labelIndex = labelIndex,
    srcReferenceType = srcReferenceType,
    dstReferenceType = dstReferenceType,
)

fun returnInstruction() = ControlInstruction.Return

fun returnCallInstruction(
    functionIndex: Index.FunctionIndex = functionIndex(),
) = ControlInstruction.ReturnCall(
    functionIndex = functionIndex,
)

fun returnCallRefInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.ReturnCallRef(
    typeIndex = typeIndex,
)

fun callInstruction(
    functionIndex: Index.FunctionIndex = functionIndex(),
) = ControlInstruction.Call(
    functionIndex = functionIndex,
)

fun callRefInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ControlInstruction.CallRef(
    typeIndex = typeIndex,
)

fun callIndirectInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    tableIndex: Index.TableIndex = tableIndex(),
) = ControlInstruction.CallIndirect(
    typeIndex = typeIndex,
    tableIndex = tableIndex,
)

fun returnCallIndirectInstruction(
    typeIndex: Index.TypeIndex = typeIndex(),
    tableIndex: Index.TableIndex = tableIndex(),
) = ControlInstruction.ReturnCallIndirect(
    typeIndex = typeIndex,
    tableIndex = tableIndex,
)

fun prefixedControlInstruction() = brOnCastInstruction()
