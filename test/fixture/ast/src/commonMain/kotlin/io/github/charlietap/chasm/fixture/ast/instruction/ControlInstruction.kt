package io.github.charlietap.chasm.fixture.ast.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.type.referenceType
import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

fun controlInstruction(): ControlInstruction = unreachableInstruction()

fun blockType(): BlockType = emptyBlockType()

fun emptyBlockType() = BlockType.Empty

fun valueBlockType(
    valueType: ValueType = valueType(),
) = BlockType.ValType(
    valueType = valueType,
)

fun signedTypeIndexBlockType(
    typeIndex: Int = 0,
) = BlockType.SignedTypeIndex(
    typeIndex = typeIndex,
)

fun catchHandler(): ControlInstruction.CatchHandler = catchCatchHandler()

fun catchCatchHandler(
    tagIndex: Index.TagIndex = tagIndex(),
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.CatchHandler.Catch(
    tagIndex = tagIndex,
    labelIndex = labelIndex,
)

fun catchRefHandler(
    tagIndex: Index.TagIndex = tagIndex(),
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.CatchHandler.CatchRef(
    tagIndex = tagIndex,
    labelIndex = labelIndex,
)

fun catchAllHandler(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.CatchHandler.CatchAll(
    labelIndex = labelIndex,
)

fun catchAllRefHandler(
    labelIndex: Index.LabelIndex = labelIndex(),
) = ControlInstruction.CatchHandler.CatchAllRef(
    labelIndex = labelIndex,
)

fun unreachableInstruction() = ControlInstruction.Unreachable

fun nopInstruction() = ControlInstruction.Nop

fun blockInstruction(
    blockType: BlockType = blockType(),
    instructions: List<Instruction> = emptyList(),
) = ControlInstruction.Block(
    blockType = blockType,
    instructions = instructions,
)

fun loopInstruction(
    blockType: BlockType = blockType(),
    instructions: List<Instruction> = emptyList(),
) = ControlInstruction.Loop(
    blockType = blockType,
    instructions = instructions,
)

fun ifInstruction(
    blockType: BlockType = blockType(),
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
