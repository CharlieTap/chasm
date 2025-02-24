package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.ControlInstruction as IRControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction as IRInstruction
import io.github.charlietap.chasm.ir.module.Index.FunctionIndex as IRFunctionIndex
import io.github.charlietap.chasm.ir.module.Index.LabelIndex as IRLabelIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex as IRTableIndex
import io.github.charlietap.chasm.ir.module.Index.TagIndex as IRTagIndex
import io.github.charlietap.chasm.ir.module.Index.TypeIndex as IRTypeIndex

internal fun ControlInstructionFactory(
    instruction: ControlInstruction,
): IRControlInstruction = ControlInstructionFactory(
    instruction = instruction,
    functionIndexFactory = ::FunctionIndexFactory,
    labelIndexFactory = ::LabelIndexFactory,
    tableIndexFactory = ::TableIndexFactory,
    tagIndexFactory = ::TagIndexFactory,
    typeIndexFactory = ::TypeIndexFactory,
    instructionFactory = ::InstructionFactory,
)

internal inline fun ControlInstructionFactory(
    instruction: ControlInstruction,
    functionIndexFactory: IRFactory<Index.FunctionIndex, IRFunctionIndex>,
    labelIndexFactory: IRFactory<Index.LabelIndex, IRLabelIndex>,
    tableIndexFactory: IRFactory<Index.TableIndex, IRTableIndex>,
    tagIndexFactory: IRFactory<Index.TagIndex, IRTagIndex>,
    typeIndexFactory: IRFactory<Index.TypeIndex, IRTypeIndex>,
    instructionFactory: IRFactory<Instruction, IRInstruction>,
): IRControlInstruction {
    return when (instruction) {
        ControlInstruction.Unreachable -> IRControlInstruction.Unreachable

        ControlInstruction.Nop -> IRControlInstruction.Nop

        is ControlInstruction.Block -> IRControlInstruction.Block(
            blockType = instruction.blockType,
            instructions = instruction.instructions.map(instructionFactory),
        )

        is ControlInstruction.Loop -> IRControlInstruction.Loop(
            blockType = instruction.blockType,
            instructions = instruction.instructions.map(instructionFactory),
        )

        is ControlInstruction.If -> IRControlInstruction.If(
            blockType = instruction.blockType,
            thenInstructions = instruction.thenInstructions.map(instructionFactory),
            elseInstructions = instruction.elseInstructions?.map(instructionFactory),
        )

        is ControlInstruction.TryTable -> IRControlInstruction.TryTable(
            blockType = instruction.blockType,
            handlers = instruction.handlers.map { CatchHandlerFactory(it, labelIndexFactory, tagIndexFactory) },
            instructions = instruction.instructions.map(instructionFactory),
        )

        is ControlInstruction.Throw -> IRControlInstruction.Throw(
            tagIndex = tagIndexFactory(instruction.tagIndex),
        )

        ControlInstruction.ThrowRef -> IRControlInstruction.ThrowRef

        is ControlInstruction.Br -> IRControlInstruction.Br(
            labelIndex = labelIndexFactory(instruction.labelIndex),
        )

        is ControlInstruction.BrIf -> IRControlInstruction.BrIf(
            labelIndex = labelIndexFactory(instruction.labelIndex),
        )

        is ControlInstruction.BrTable -> IRControlInstruction.BrTable(
            labelIndices = instruction.labelIndices.map(labelIndexFactory),
            defaultLabelIndex = labelIndexFactory(instruction.defaultLabelIndex),
        )

        is ControlInstruction.BrOnNull -> IRControlInstruction.BrOnNull(
            labelIndex = labelIndexFactory(instruction.labelIndex),
        )

        is ControlInstruction.BrOnNonNull -> IRControlInstruction.BrOnNonNull(
            labelIndex = labelIndexFactory(instruction.labelIndex),
        )

        is ControlInstruction.BrOnCast -> IRControlInstruction.BrOnCast(
            labelIndex = labelIndexFactory(instruction.labelIndex),
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        )

        is ControlInstruction.BrOnCastFail -> IRControlInstruction.BrOnCastFail(
            labelIndex = labelIndexFactory(instruction.labelIndex),
            srcReferenceType = instruction.srcReferenceType,
            dstReferenceType = instruction.dstReferenceType,
        )

        ControlInstruction.Return -> IRControlInstruction.Return

        is ControlInstruction.ReturnCall -> IRControlInstruction.ReturnCall(
            functionIndex = functionIndexFactory(instruction.functionIndex),
        )

        is ControlInstruction.ReturnCallRef -> IRControlInstruction.ReturnCallRef(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is ControlInstruction.Call -> IRControlInstruction.Call(
            functionIndex = functionIndexFactory(instruction.functionIndex),
        )

        is ControlInstruction.CallRef -> IRControlInstruction.CallRef(
            typeIndex = typeIndexFactory(instruction.typeIndex),
        )

        is ControlInstruction.CallIndirect -> IRControlInstruction.CallIndirect(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            tableIndex = tableIndexFactory(instruction.tableIndex),
        )

        is ControlInstruction.ReturnCallIndirect -> IRControlInstruction.ReturnCallIndirect(
            typeIndex = typeIndexFactory(instruction.typeIndex),
            tableIndex = tableIndexFactory(instruction.tableIndex),
        )
    }
}

internal inline fun CatchHandlerFactory(
    handler: ControlInstruction.CatchHandler,
    labelIndexFactory: IRFactory<Index.LabelIndex, IRLabelIndex>,
    tagIndexFactory: IRFactory<Index.TagIndex, IRTagIndex>,
): IRControlInstruction.CatchHandler {
    return when (handler) {
        is ControlInstruction.CatchHandler.Catch -> IRControlInstruction.CatchHandler.Catch(
            tagIndex = tagIndexFactory(handler.tagIndex),
            labelIndex = labelIndexFactory(handler.labelIndex),
        )
        is ControlInstruction.CatchHandler.CatchRef -> IRControlInstruction.CatchHandler.CatchRef(
            tagIndex = tagIndexFactory(handler.tagIndex),
            labelIndex = labelIndexFactory(handler.labelIndex),
        )
        is ControlInstruction.CatchHandler.CatchAll -> IRControlInstruction.CatchHandler.CatchAll(
            labelIndex = labelIndexFactory(handler.labelIndex),
        )
        is ControlInstruction.CatchHandler.CatchAllRef -> IRControlInstruction.CatchHandler.CatchAllRef(
            labelIndex = labelIndexFactory(handler.labelIndex),
        )
    }
}
